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
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.util.DateUtil;

@Service
public class Step2Service {

        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final Map<String, List<RateAdjustRule>> rateAdjustMap;
        private final Map<String, Expense> expenseMap;

        public Step2Service(CalcMasterData master) {
                this.rateMap = master.rateMap;
                this.mgrMap = master.mgrMap;
                this.rateAdjustMap = master.rateAdjustMap;
                this.expenseMap = master.expenseMap;
        }

        /**
         * Step2 단일 계약 계산
         */
        public Step2Summary calculateOne(
                        CalcRunContext runCtx,
                        Step1Summary s) {

                // ---- step2End = MIN(calcBaseDate, annuityDate)
                LocalDate step2End = CalcBaseDateResolver.resolve(runCtx, s.contractDate);

                if (step2End.isAfter(s.annuityDate)) {
                        step2End = s.annuityDate;
                }

                Expense exp = expenseMap.get(s.expenseKey);
                if (exp == null) {
                        throw new IllegalStateException(
                                        "Expense not found: " + s.expenseKey);
                }

                CalcContext ctx = new CalcContext();
                ctx.plyNo = s.plyNo;
                ctx.contractDate = s.contractDate;
                ctx.rateArr = rateMap.get(s.rateCode);
                ctx.mgrArr = mgrMap.get(s.productCode);
                ctx.rateAdjustRules = rateAdjustMap.get(s.rateCode);
                ctx.debugMode = runCtx.debugMode;
                ctx.applyTag = "STEP2";

                // =================================================
                // (A) 월 사업비 CF 누적
                // =================================================
                double totalMonthlyExpenseAcc = 0.0;
                LocalDate mStart = s.step1EndDate;
                int chargeDay = s.contractDate.getDayOfMonth();

                if (exp.monthlyAmount != 0.0) {

                        while (mStart.isBefore(step2End)) {

                                double expAmt = -exp.monthlyAmount;
                                ctx.applyTag = "EXP_M";
                                ctx.principal = expAmt;

                                double factor = CalcFactorByYear.calc(
                                                ctx,
                                                mStart,
                                                step2End);

                                totalMonthlyExpenseAcc += expAmt * factor;

                                mStart = DateUtil.nextMonthlyDate(mStart, chargeDay);
                        }
                }

                // =================================================
                // (B) 연 기준 잔액 스냅샷
                // =================================================
                double bal = s.netBalance;
                LocalDate yearStart = s.step1EndDate;

                while (yearStart.isBefore(step2End)) {

                        LocalDate yearEnd = yearStart.plusYears(1);
                        if (yearEnd.isAfter(step2End)) {
                                yearEnd = step2End;
                        }

                        double annualCF = 0.0;

                        int expectedDay = DateUtil.clampDayToEOM(
                                        yearStart.getYear(),
                                        yearStart.getMonthValue(),
                                        chargeDay);

                        if (yearStart.getMonthValue() == s.contractDate.getMonthValue()
                                        && yearStart.getDayOfMonth() == expectedDay) {
                                annualCF = -bal * exp.yearlyRate / 100.0;
                        }

                        double baseAfterAnnual = bal + annualCF;

                        ctx.applyTag = "Principal";
                        ctx.principal = baseAfterAnnual;

                        double factorYear = CalcFactorByYear.calc(
                                        ctx,
                                        yearStart,
                                        yearEnd);

                        bal = baseAfterAnnual * factorYear;
                        yearStart = yearEnd;
                }

                double baseEndBalance = bal;
                double finalEndBalance = baseEndBalance + totalMonthlyExpenseAcc;

                return new Step2Summary(
                                s.plyNo,
                                finalEndBalance,
                                step2End,
                                s.annuityDate,
                                s.rateCode,
                                s.productCode,
                                s.expenseKey,
                                s.annuityTerm,
                                s.insEndDate,
                                s.contractDate,
                                baseEndBalance,
                                totalMonthlyExpenseAcc);
        }
}
