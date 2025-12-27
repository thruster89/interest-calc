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
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.domain.Step3Detail;
import com.interestcalc.domain.Step3Summary;
import com.interestcalc.util.AnnuityUtil;
import com.interestcalc.util.DateUtil;

public class Step3Service {

        // ============================
        // Result container
        // ============================
        public static class Result {
                public List<Step3Detail> details = new ArrayList<>();
                public List<Step3Summary> summaries = new ArrayList<>();
        }

        // ============================
        // Main entry
        // ============================
        public Result run(
                        List<Step2Summary> inputs,
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        Map<String, Expense> expenseMap,
                        CalcBaseDateType baseDateType,
                        LocalDate calcBaseDate,
                        int calcBaseYear,
                        boolean debugMode) {

                Result result = new Result();

                for (Step2Summary s2 : inputs) {

                        String plyNo = s2.plyNo;

                        if (baseDateType == CalcBaseDateType.CONTRACT_MMDD) {
                                calcBaseDate = LocalDate.of(
                                                calcBaseYear,
                                                s2.insStartDate.getMonth(),
                                                s2.insStartDate.getDayOfMonth());
                        }

                        LocalDate curDate = s2.annuityDate;
                        LocalDate endDate = calcBaseDate.isAfter(s2.insClusterDate)
                                        ? s2.insClusterDate
                                        : calcBaseDate;

                        double bal = s2.balance;

                        // 계산 대상 아님
                        if (curDate.isAfter(endDate)) {
                                result.summaries.add(
                                                Step3Summary.of(s2, bal, calcBaseDate));
                                continue;
                        }

                        List<RateSegment> rateArr = rateMap.get(s2.rateCode);
                        List<MinGuaranteedRateSegment> mgrArr = mgrMap.get(s2.productCode);

                        Expense exp = expenseMap.get(s2.expenseKey);
                        double yearlyExpRate = exp != null ? exp.yearlyRate : 0.0;

                        // ============================
                        // Year loop
                        // ============================
                        for (int yearIdx = 0; yearIdx < s2.annuityTerm; yearIdx++) {

                                if (!curDate.isBefore(endDate))
                                        // if (!endDate.isBefore(curDate))
                                        break;

                                double beginBal = bal;

                                // --------------------------------------------------
                                // ① 연금용 할인율 (그 시점 단일 값)
                                // --------------------------------------------------

                                // base rate (date point)
                                double baseRate = 0.0;
                                for (RateSegment r : rateArr) {
                                        if (r.contains(curDate)) {
                                                baseRate = r.getRate();
                                                break;
                                        }
                                }

                                // mgr rate (elapsed policy year)
                                long elapsedYear = DateUtil.elapsedYears(
                                                s2.insStartDate, curDate);

                                double mgrRate = 0.0;
                                if (mgrArr != null) {
                                        int y = (int) elapsedYear;
                                        for (MinGuaranteedRateSegment m : mgrArr) {
                                                if (m.matches(y)) {
                                                        mgrRate = m.getRate();
                                                        break;
                                                }
                                        }
                                }

                                double annRate = Math.max(baseRate, mgrRate);
                                double v = 1.0 / (1.0 + annRate / 100.0);

                                // --------------------------------------------------
                                // ② 연금 / 사업비 (연초)
                                // --------------------------------------------------
                                int remainYears = (int) (s2.annuityTerm - yearIdx);

                                double annAmt = remainYears > 0
                                                ? AnnuityUtil.calcAnnuityAmount(bal, v, remainYears)
                                                                / (1.0 + yearlyExpRate * 0.01)
                                                : 0.0;

                                double annualExp = annAmt * yearlyExpRate * 0.01;

                                // 연초 차감
                                bal = bal - annAmt - annualExp;

                                // --------------------------------------------------
                                // ③ 연말 이자 (CalcFactorByYear에 전부 위임)
                                // --------------------------------------------------
                                CalcContext ctx = new CalcContext();
                                ctx.plyNo = plyNo;
                                ctx.contractDate = s2.insStartDate;
                                ctx.rateArr = rateArr;
                                ctx.mgrArr = mgrArr;
                                ctx.principal = bal;
                                ctx.rateAdj = 0.0;
                                ctx.debugMode = debugMode;

                                LocalDate nextDate = curDate.plusYears(1);
                                if (nextDate.isAfter(endDate)) {
                                        nextDate = endDate;
                                }

                                double factor = FactorCache.getFactor(ctx, curDate, nextDate);
                                double interest = bal * (factor - 1.0);
                                bal += interest;

                                // --------------------------------------------------
                                // Detail
                                // --------------------------------------------------
                                Step3Detail d = new Step3Detail();
                                d.plyNo = plyNo;
                                d.fromDate = curDate;
                                d.toDate = nextDate;
                                d.beginBalance = beginBal;
                                d.annuityAmount = annAmt;
                                d.annualExpense = annualExp;
                                d.interest = interest;
                                d.endBalance = bal;
                                d.factor = factor;

                                result.details.add(d);

                                curDate = curDate.plusYears(1);
                        }

                        // ============================
                        // Summary
                        // ============================
                        result.summaries.add(
                                        Step3Summary.of(s2, bal, calcBaseDate));
                }

                return result;
        }
}
