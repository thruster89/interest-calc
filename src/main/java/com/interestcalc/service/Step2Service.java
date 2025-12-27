package com.interestcalc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.interestcalc.calc.FactorCache;
import com.interestcalc.context.CalcBaseDateType;
import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.Expense;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Detail;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.util.DateUtil;

public class Step2Service {

        // ============================
        // Result container
        // ============================
        public static class Result {
                public List<Step2Detail> details = new ArrayList<>();
                public List<Step2Summary> summaries = new ArrayList<>();
        }

        // ============================
        // Main entry
        // ============================
        public Result run(
                        List<Step1Summary> inputs,
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        Map<String, Expense> expenseMap,
                        String runMode,
                        String targetPlyNo,
                        CalcBaseDateType baseDateType,
                        LocalDate calcBaseDate,
                        int calcBaseYear,
                        boolean debugMode) {

                Result result = new Result();

                for (Step1Summary s1 : inputs) {

                        String plyNo = s1.plyNo;

                        if ("ONE".equals(runMode) && !plyNo.equals(targetPlyNo)) {
                                continue;
                        }

                        // ============================
                        // 기간 결정
                        // ============================
                        LocalDate step2End = calcBaseDate;
                        if (step2End.isAfter(s1.annuityDate)) {
                                step2End = s1.annuityDate;
                        }

                        if (!step2End.isAfter(s1.step1EndDate)) {
                                Step2Summary sum = new Step2Summary();

                                sum.plyNo = s1.plyNo;
                                sum.balance = s1.balance; // ★ Step2 결과
                                sum.calcBaseDate = step2End; // ★ Step2 종료일

                                sum.annuityDate = s1.annuityDate;
                                sum.insStartDate = s1.contractDate;
                                sum.insClusterDate = s1.insEndDate;

                                sum.rateCode = s1.rateCode;
                                sum.productCode = s1.productCode;
                                sum.expenseKey = s1.expenseKey;
                                sum.annuityTerm = s1.annuityTerm;

                                result.summaries.add(sum);
                                continue;
                        }

                        // ============================
                        // Master lookup
                        // ============================
                        List<RateSegment> rateArr = rateMap.get(s1.rateCode);
                        List<MinGuaranteedRateSegment> mgrArr = mgrMap.get(s1.productCode);
                        Expense exp = expenseMap.get(s1.expenseKey);

                        if (rateArr == null)
                                throw new RuntimeException("Rate not found: " + s1.rateCode);
                        if (exp == null)
                                throw new RuntimeException("Expense not found: " + s1.expenseKey);

                        // ============================
                        // 연 단위 기준 원금
                        // ============================
                        double basePrincipal = s1.balance;
                        // double accInterest = 0.0;
                        // double accExpense = 0.0;

                        LocalDate yearStartDate = s1.step1EndDate; // ★ 연초 고정
                        LocalDate curDate = s1.step1EndDate;

                        int contractDay = s1.contractDate.getDayOfMonth();
                        int contractMonth = s1.contractDate.getMonthValue();

                        if (debugMode) {
                                System.out.println("\n================ STEP2 START =================");
                                System.out.println("PLYNO=" + plyNo);
                                System.out.println("BASE_PRINCIPAL=" + basePrincipal);
                                System.out.println("FROM=" + curDate + " TO=" + step2End);
                        }

                        // ============================
                        // Monthly loop
                        // ============================
                        while (curDate.isBefore(step2End)) {

                                LocalDate nextDate = DateUtil.nextMonthlyDate(curDate, contractDay);
                                if (nextDate.isAfter(step2End)) {
                                        nextDate = step2End;
                                }

                                // ----------------------------
                                // Expense
                                // ----------------------------
                                double monthlyExp = exp.monthlyAmount;
                                double annualExp;

                                // accExpense += monthlyExp;

                                // 연 사업비: 계약 MM-DD
                                int cd = DateUtil.clampDayToEOM(
                                                curDate.getYear(),
                                                contractMonth,
                                                contractDay);

                                annualExp = basePrincipal * exp.yearlyRate * 0.01;
                                // accExpense += annualExp;
                                // }

                                // ----------------------------
                                // Interest
                                // ----------------------------
                                CalcContext ctx = new CalcContext();
                                ctx.plyNo = plyNo;
                                ctx.contractDate = s1.contractDate;
                                ctx.rateArr = rateArr;
                                ctx.mgrArr = mgrArr;
                                ctx.principal = basePrincipal;
                                ctx.rateAdj = 0.0;
                                ctx.debugMode = debugMode;

                                double factor = FactorCache.getFactor(ctx, yearStartDate, nextDate);
                                double interest = (basePrincipal - annualExp) * (factor - 1.0);
                                // accInterest += interest;

                                double endBal = basePrincipal - annualExp + interest;

                                // ----------------------------
                                // Detail
                                // ----------------------------
                                Step2Detail d = new Step2Detail();
                                d.plyNo = plyNo;
                                d.fromDate = curDate;
                                d.toDate = nextDate;
                                d.beginBalance = basePrincipal;
                                d.annualExpense = annualExp;
                                d.monthlyExpense = monthlyExp;
                                d.interest = interest;
                                d.endBalance = endBal;
                                d.factor = factor;

                                result.details.add(d);

                                if (debugMode) {
                                        System.out.printf(
                                                        """
                                                                        [M] %s ~ %s | BASE=%,.0f INT=%,.0f ANN_EXP=%,.0f MON_EXP=%,.0f END=%,.0f
                                                                        """,
                                                        curDate, nextDate,
                                                        basePrincipal,
                                                        interest,
                                                        annualExp,
                                                        monthlyExp,
                                                        endBal);
                                }

                                // ----------------------------
                                // 연 기준 원금 갱신
                                // ----------------------------
                                if (nextDate.getMonthValue() == contractMonth
                                                && nextDate.getDayOfMonth() == cd) {

                                        basePrincipal = endBal;
                                        // accInterest = 0.0;
                                        // accExpense = 0.0;
                                        yearStartDate = nextDate;
                                        if (debugMode) {
                                                System.out.println(">>> YEAR RESET");
                                                System.out.println("NEW BASE=" + basePrincipal);
                                        }

                                }
                                curDate = nextDate;

                        }

                        // ============================
                        // Summary
                        // ============================
                        Step2Summary sum = new Step2Summary();

                        sum.plyNo = s1.plyNo;
                        sum.balance = basePrincipal; // ★ Step2 결과
                        sum.calcBaseDate = step2End; // ★ Step2 종료일

                        sum.annuityDate = s1.annuityDate;
                        sum.insStartDate = s1.contractDate;
                        sum.insClusterDate = s1.insEndDate;

                        sum.rateCode = s1.rateCode;
                        sum.productCode = s1.productCode;
                        sum.expenseKey = s1.expenseKey;
                        sum.annuityTerm = s1.annuityTerm;

                        result.summaries.add(sum);

                }

                return result;
        }
}
