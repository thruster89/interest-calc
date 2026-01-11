package com.interestcalc.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interestcalc.calc.CalcBaseDateResolver;
import com.interestcalc.calc.CalcFactorByYear;
import com.interestcalc.context.CalcContext;
import com.interestcalc.context.CalcMasterData;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Expense;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.domain.Step3Summary;

@Service
public class Step3Service {

        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final Map<String, List<RateAdjustRule>> rateAdjustMap;
        private final Map<String, Expense> expenseMap;

        public Step3Service(CalcMasterData master) {
                this.rateMap = master.rateMap;
                this.mgrMap = master.mgrMap;
                this.rateAdjustMap = master.rateAdjustMap;
                this.expenseMap = master.expenseMap;
        }

        /**
         * Step3 단일 계약 계산 (연금지급기간)
         */
        public Step3Summary calculateOne(
                        CalcRunContext runCtx,
                        Step2Summary s) {

                LocalDate annuityDate = s.annuityDate;
                LocalDate insEndDate = s.insEndDate;

                // STEP3_END = MIN(calcBaseDate, insEndDate)
                LocalDate calcBaseDate = CalcBaseDateResolver.resolve(runCtx, s.contractDate);
                LocalDate step3End = calcBaseDate.isBefore(insEndDate)
                                ? calcBaseDate
                                : insEndDate;

                // 종료 구간이면 바로 반환 (VBA 동일)
                if (!step3End.isAfter(annuityDate)) {
                        return new Step3Summary(
                                        s.plyNo,
                                        s.balance,
                                        step3End,
                                        s.rateCode,
                                        s.productCode,
                                        s.expenseKey,
                                        s.annuityTerm);
                }

                Expense exp = expenseMap.get(s.expenseKey);
                if (exp == null) {
                        throw new IllegalStateException("Expense not found: " + s.expenseKey);
                }
                double annualExpRate = exp.yearlyRate / 100.0;

                // =================================================
                // CalcContext (계약 단위)
                // =================================================
                CalcContext ctx = new CalcContext();
                ctx.plyNo = s.plyNo;
                ctx.contractDate = s.contractDate;
                ctx.rateArr = rateMap.get(s.rateCode);
                ctx.mgrArr = mgrMap.get(s.productCode);
                ctx.rateAdjustRules = rateAdjustMap.get(s.rateCode);
                ctx.debugMode = runCtx.debugMode;
                ctx.applyTag = "STEP3";

                double reserve = s.balance;
                LocalDate curDate = annuityDate;
                double endBal = reserve;

                // =================================================
                // 연금 지급 루프
                // =================================================
                for (int yearIdx = 1; yearIdx <= s.annuityTerm; yearIdx++) {

                        if (!curDate.isBefore(step3End)) {
                                break;
                        }

                        LocalDate fromDate = curDate;
                        LocalDate toDate = (yearIdx == 1)
                                        ? fromDate // 최초 1회 (이자 없음)
                                        : fromDate.plusYears(1);

                        if (toDate.isAfter(step3End)) {
                                toDate = step3End;
                        }

                        double beginBal = reserve;

                        // ===== Interest
                        ctx.applyTag = "INTEREST";
                        ctx.principal = beginBal;

                        double factor;
                        double interest;

                        if (fromDate.isBefore(toDate)) {
                                factor = CalcFactorByYear.calc(ctx, fromDate, toDate);
                                interest = beginBal * (factor - 1.0);
                        } else {
                                factor = 1.0;
                                interest = 0.0;
                        }

                        double midBal = beginBal + interest;

                        // ===== Annuity / Expense
                        double annAmt = 0.0;
                        double annualExp = 0.0;

                        boolean isPaymentDate = toDate.getMonthValue() == s.contractDate.getMonthValue()
                                        && toDate.getDayOfMonth() == s.contractDate.getDayOfMonth();

                        int remainYears = s.annuityTerm - yearIdx + 1;

                        if (isPaymentDate && remainYears > 0) {

                                double baseRate = resolveBaseRate(ctx.rateArr, toDate);
                                double disc = 1.0 / (1.0 + baseRate);

                                annAmt = calcAnnuityAmount(midBal, disc, remainYears)
                                                / (1.0 + annualExpRate);

                                annualExp = annAmt * annualExpRate;
                        }

                        endBal = midBal - annAmt - annualExp;

                        reserve = endBal;
                        curDate = toDate;
                }

                return new Step3Summary(
                                s.plyNo,
                                endBal,
                                step3End,
                                s.rateCode,
                                s.productCode,
                                s.expenseKey,
                                s.annuityTerm);
        }

        // =====================================================
        // Helpers (VBA 대응)
        // =====================================================
        private static double resolveBaseRate(
                        List<RateSegment> rateArr,
                        LocalDate asOfDate) {

                for (RateSegment r : rateArr) {
                        if (!asOfDate.isBefore(r.getFromDate())
                                        && asOfDate.isBefore(r.getToDate())) {
                                return r.getRate();
                        }
                }
                return 0.0;
        }

        private static double calcAnnuityAmount(
                        double balance,
                        double disc,
                        int remainYears) {

                if (remainYears <= 0) {
                        return 0.0;
                }
                return balance * (1.0 - disc)
                                / (1.0 - Math.pow(disc, remainYears));
        }
}
