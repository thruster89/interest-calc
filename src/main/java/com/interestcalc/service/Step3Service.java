package com.interestcalc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.interestcalc.calc.CalcBaseDateResolver;
import com.interestcalc.calc.CalcFactorByYear;
import com.interestcalc.context.CalcContext;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.domain.Expense;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.domain.Step3Detail;
import com.interestcalc.domain.Step3Summary;

public class Step3Service {

        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final Map<String, List<RateAdjustRule>> rateAdjustMap;
        private final Map<String, Expense> expenseMap;

        public Step3Service(
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        Map<String, List<RateAdjustRule>> rateAdjustMap,
                        Map<String, Expense> expenseMap) {

                this.rateMap = rateMap;
                this.mgrMap = mgrMap;
                this.expenseMap = expenseMap;
                this.rateAdjustMap = rateAdjustMap;
        }

        // =====================================================
        // Result
        // =====================================================
        public record Result(
                        List<Step3Detail> details,
                        List<Step3Summary> summaries,
                        List<CalcDebugRow> debugRows) {
        }

        // =====================================================
        // RUN
        // =====================================================
        public Result run(
                        CalcRunContext runCtx,
                        List<Step2Summary> inputs) {

                List<Step3Detail> details = new ArrayList<>();
                List<Step3Summary> summaries = new ArrayList<>();
                List<CalcDebugRow> allDebugRows = runCtx.debugMode ? new ArrayList<>() : List.of();

                for (Step2Summary s : inputs) {

                        LocalDate annuityDate = s.annuityDate;
                        LocalDate insEndDate = s.insEndDate;
                        LocalDate calcBaseDate = CalcBaseDateResolver.resolve(runCtx, s.contractDate);

                        // 종료 체크 (VBA 동일)
                        if (calcBaseDate.isBefore(annuityDate)
                                        || calcBaseDate.isAfter(insEndDate)) {

                                summaries.add(new Step3Summary(
                                                s.plyNo,
                                                s.balance,
                                                calcBaseDate,
                                                s.rateCode,
                                                s.productCode,
                                                s.expenseKey,
                                                s.annuityTerm));

                                continue;
                        }

                        Expense exp = expenseMap.get(s.expenseKey);
                        double annualExpRate = exp.yearlyRate / 100.0;

                        // ===== CalcContext (계약 단위) =====
                        CalcContext ctx = new CalcContext();
                        ctx.plyNo = s.plyNo;
                        ctx.contractDate = s.contractDate;
                        ctx.rateArr = rateMap.get(s.rateCode);
                        ctx.mgrArr = mgrMap.get(s.productCode);
                        ctx.rateAdjustRules = rateAdjustMap.get(s.rateCode);
                        ctx.debugMode = runCtx.debugMode;
                        ctx.applyTag = "STEP3";

                        if (ctx.debugMode) {
                                ctx.debugRows = new ArrayList<>();
                        }

                        // ===== Year loop =====
                        double reserve = s.balance;
                        LocalDate curDate = annuityDate;
                        double endBal = reserve;

                        for (int yearIdx = 1; yearIdx <= s.annuityTerm; yearIdx++) {

                                if (!curDate.isBefore(calcBaseDate)
                                                || !curDate.isBefore(insEndDate)) {
                                        break;
                                }

                                LocalDate fromDate = curDate;
                                LocalDate toDate;

                                if (yearIdx == 1) {
                                        toDate = curDate; // 최초 1회
                                } else {
                                        toDate = curDate.plusYears(1);
                                }

                                if (toDate.isAfter(calcBaseDate))
                                        toDate = calcBaseDate;
                                if (toDate.isAfter(insEndDate))
                                        toDate = insEndDate;

                                double beginBal = reserve;

                                // ===== Interest =====
                                double factor;
                                double interest;

                                ctx.applyTag = "INTEREST";
                                ctx.principal = beginBal;

                                if (fromDate.isBefore(toDate)) {
                                        factor = CalcFactorByYear.calc(ctx, fromDate, toDate);
                                        interest = beginBal * (factor - 1.0);
                                } else {
                                        factor = 1.0;
                                        interest = 0.0;
                                }

                                double midBal = beginBal + interest;

                                // ===== ANN / EXP (TO_DATE 시점) =====
                                double annAmt = 0.0;
                                double annualExp = 0.0;

                                boolean isPaymentDate = toDate.getMonthValue() == s.contractDate.getMonthValue()
                                                && toDate.getDayOfMonth() == s.contractDate.getDayOfMonth();

                                int remainYears = s.annuityTerm - yearIdx + 1;
                                double disc = 0.0;

                                if (isPaymentDate) {
                                        double baseRate = resolveBaseRate(ctx.rateArr, toDate);
                                        disc = 1.0 / (1.0 + baseRate);

                                        annAmt = calcAnnuityAmount(midBal, disc, remainYears)
                                                        / (1.0 + annualExpRate);

                                        annualExp = annAmt * annualExpRate;
                                }

                                endBal = midBal - annAmt - annualExp;

                                // ===== Detail =====
                                details.add(new Step3Detail(
                                                s.plyNo,
                                                fromDate,
                                                toDate,
                                                beginBal,
                                                interest,
                                                annAmt,
                                                annualExp,
                                                endBal,
                                                factor,
                                                yearIdx,
                                                remainYears,
                                                disc));

                                reserve = endBal;
                                curDate = toDate;
                        }

                        if (ctx.debugMode) {
                                allDebugRows.addAll(ctx.debugRows);
                        }

                        // ===== Summary =====
                        summaries.add(new Step3Summary(
                                        s.plyNo,
                                        endBal,
                                        calcBaseDate,
                                        s.rateCode,
                                        s.productCode,
                                        s.expenseKey,
                                        s.annuityTerm));
                }

                return new Result(details, summaries, allDebugRows);
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

                if (remainYears <= 0)
                        return 0.0;
                return balance * (1.0 - disc) / (1.0 - Math.pow(disc, remainYears));
        }
}
