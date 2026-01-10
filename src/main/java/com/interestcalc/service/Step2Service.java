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
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Detail;
import com.interestcalc.domain.Step2ExpDetail;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.util.DateUtil;

public class Step2Service {

        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final Map<String, List<RateAdjustRule>> rateAdjustMap;
        private final Map<String, Expense> expenseMap;

        public Step2Service(
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        Map<String, List<RateAdjustRule>> rateAdjustMap,
                        Map<String, Expense> expenseMap) {

                this.rateMap = rateMap;
                this.mgrMap = mgrMap;
                this.rateAdjustMap = rateAdjustMap;
                this.expenseMap = expenseMap;
        }

        public record Result(
                        List<Step2Detail> details,
                        List<Step2ExpDetail> expDetails,
                        List<Step2Summary> summaries,
                        List<CalcDebugRow> debugRows) {
        }

        public Result run(
                        CalcRunContext runCtx,
                        List<Step1Summary> step1Summaries) {

                List<Step2Detail> details = new ArrayList<>();
                List<Step2ExpDetail> expDetails = new ArrayList<>();
                List<Step2Summary> summaries = new ArrayList<>();
                List<CalcDebugRow> allDebugRows = runCtx.debugMode ? new ArrayList<>() : List.of();

                // =================================================
                // Loop contracts (Step1_Summary 기준)
                // =================================================
                for (Step1Summary s : step1Summaries) {

                        // ---- step2End = MIN(calcBaseDate, annuityDate)
                        LocalDate step2End = CalcBaseDateResolver.resolve(runCtx, s.contractDate);
                        if (step2End.isAfter(s.annuityDate)) {
                                step2End = s.annuityDate;
                        }
                        if (!step2End.isAfter(s.step1EndDate)) {
                                continue;
                        }

                        Expense exp = expenseMap.get(s.expenseKey);
                        if (exp == null) {
                                throw new IllegalStateException(
                                                "Expense not found: " + s.expenseKey);
                        }

                        // =================================================
                        // ctx (계약당 1개) ★★★ 절대 재생성 안 함 ★★★
                        // =================================================
                        CalcContext ctx = new CalcContext();
                        ctx.plyNo = s.plyNo;
                        ctx.contractDate = s.contractDate;
                        ctx.rateArr = rateMap.get(s.rateCode);
                        ctx.mgrArr = mgrMap.get(s.productCode);
                        ctx.rateAdjustRules = rateAdjustMap.get(s.rateCode);
                        ctx.debugMode = runCtx.debugMode;
                        ctx.applyTag = "STEP2";

                        if (ctx.debugMode) {
                                ctx.debugRows = new ArrayList<>();
                        }

                        // =================================================
                        // (A) 월 사업비 CF
                        // =================================================
                        double totalMonthlyExpenseAcc = 0.0;
                        LocalDate mStart = s.step1EndDate;
                        int chargeDay = s.contractDate.getDayOfMonth();
                        long expSeq = 1;

                        if (exp.monthlyAmount != 0.0) {

                                while (mStart.isBefore(step2End)) {

                                        double expAmt = -exp.monthlyAmount;
                                        ctx.applyTag = "EXP_M";
                                        ctx.principal = expAmt;

                                        double factor = CalcFactorByYear.calc(
                                                        ctx,
                                                        mStart,
                                                        step2End);

                                        double accAmt = expAmt * factor;

                                        expDetails.add(new Step2ExpDetail(
                                                        s.plyNo,
                                                        expSeq,
                                                        mStart,
                                                        expAmt,
                                                        factor,
                                                        accAmt,
                                                        step2End));

                                        totalMonthlyExpenseAcc += accAmt;
                                        expSeq++;

                                        // ★ VBA NextMonthlyDate 그대로
                                        mStart = DateUtil.nextMonthlyDate(mStart, chargeDay);
                                }
                        }

                        // =================================================
                        // (B) 연 기준 스냅샷
                        // =================================================
                        double bal = s.netBalance;
                        LocalDate yearStart = s.step1EndDate;

                        while (yearStart.isBefore(step2End)) {

                                LocalDate yearEnd = yearStart.plusYears(1);
                                if (yearEnd.isAfter(step2End)) {
                                        yearEnd = step2End;
                                }

                                double baseBalYear = bal;
                                double annualCF = 0.0;

                                int expectedDay = DateUtil.clampDayToEOM(
                                                yearStart.getYear(),
                                                yearStart.getMonthValue(),
                                                chargeDay);

                                if (yearStart.getMonthValue() == s.contractDate.getMonthValue()
                                                && yearStart.getDayOfMonth() == expectedDay) {
                                        annualCF = -baseBalYear * exp.yearlyRate / 100.0;
                                }

                                double baseAfterAnnual = baseBalYear + annualCF;

                                ctx.applyTag = "Principal";
                                ctx.principal = baseAfterAnnual;

                                double factorYear = CalcFactorByYear.calc(
                                                ctx,
                                                yearStart,
                                                yearEnd);

                                double endBal = baseAfterAnnual * factorYear;

                                details.add(new Step2Detail(
                                                s.plyNo,
                                                yearStart,
                                                yearEnd,
                                                baseBalYear,
                                                annualCF,
                                                baseAfterAnnual,
                                                factorYear,
                                                endBal));

                                bal = endBal;
                                yearStart = yearEnd;
                        }

                        // =================================================
                        // Summary
                        // =================================================
                        double baseEndBalance = bal;
                        double finalEndBalance = baseEndBalance + totalMonthlyExpenseAcc;

                        summaries.add(new Step2Summary(
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
                                        totalMonthlyExpenseAcc));

                        if (ctx.debugMode) {
                                allDebugRows.addAll(ctx.debugRows);
                        }
                }

                return new Result(details, expDetails, summaries, allDebugRows);
        }
}
