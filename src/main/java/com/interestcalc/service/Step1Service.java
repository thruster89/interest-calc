package com.interestcalc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.calc.FactorCache;
import com.interestcalc.context.CalcBaseDateType;
import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.Contract;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step1Detail;
import com.interestcalc.domain.Step1Summary;

public class Step1Service {

        private final Map<String, Contract> contractMap;
        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final boolean debugMode;

        public Step1Service(
                        Map<String, Contract> contractMap,
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        boolean debugMode) {
                this.contractMap = contractMap;
                this.rateMap = rateMap;
                this.mgrMap = mgrMap;
                this.debugMode = debugMode;
        }

        // 결과 묶음
        public static class Result {
                public final List<Step1Detail> details;
                public final Map<String, Step1Summary> summaries;

                public Result(List<Step1Detail> d, Map<String, Step1Summary> s) {
                        this.details = d;
                        this.summaries = s;
                }
        }

        public Result run(
                        List<Deposit> deposits,
                        String runMode,
                        String targetPlyNo,
                        CalcBaseDateType baseDateType,
                        LocalDate calcBaseDate,
                        int calcBaseYear) {

                List<Step1Detail> detailList = new ArrayList<>();
                Map<String, Step1Summary> summaryMap = new LinkedHashMap<>();

                for (Deposit dep : deposits) {

                        // if (debugMode) {
                        // System.out.printf(
                        // "[STEP1][START] plyNo=%s depSeq=%d depDate=%s principal=%,.2f%n",
                        // dep.getPlyNo(),
                        // dep.getDepositSeq(),
                        // dep.getDepositDate(),
                        // dep.getPrincipal());
                        // }

                        String plyNo = dep.getPlyNo();

                        if ("ONE".equals(runMode) && !plyNo.equals(targetPlyNo)) {
                                continue;
                        }

                        Contract c = contractMap.get(plyNo);
                        if (c == null) {
                                // 계약 없는 입금 → 스킵
                                if (debugMode) {
                                        System.out.println("[SKIP] Contract not found for plyNo=" + plyNo);
                                }
                                continue;
                        }

                        // ===== CalcContext =====
                        CalcContext ctx = new CalcContext();
                        ctx.plyNo = plyNo;
                        ctx.contractDate = c.getInsStartDate();
                        ctx.rateArr = rateMap.get(c.getRateCode());
                        ctx.mgrArr = mgrMap.get(c.getProductCode());
                        ctx.rateAdj = 0.0;
                        ctx.debugMode = debugMode;
                        ctx.depositSeq = dep.getDepositSeq();

                        if (baseDateType == CalcBaseDateType.CONTRACT_MMDD) {
                                calcBaseDate = LocalDate.of(
                                                calcBaseYear,
                                                c.getInsStartDate().getMonth(),
                                                c.getInsStartDate().getDayOfMonth());
                        }

                        if (ctx.rateArr == null) {
                                if (debugMode) {
                                        System.out.println("[SKIP] Rate not found: " + c.getRateCode());
                                }
                                continue;
                        }

                        if (ctx.mgrArr == null) {
                                // VBA도 MGR 없는 상품 허용 → 0%
                                ctx.mgrArr = List.of(); // 빈 리스트
                        }

                        // ===== STEP1 기간 =====
                        LocalDate payEndDate = c.getInsStartDate().plusYears(c.getPayYears());

                        LocalDate step1EndDate = calcBaseDate.isBefore(payEndDate) ? calcBaseDate : payEndDate;

                        // ===== 계산 =====
                        double factor = FactorCache.getFactor(
                                        ctx,
                                        dep.getDepositDate(),
                                        step1EndDate);

                        double balance = dep.getPrincipal() * factor;

                        if (debugMode) {
                                System.out.printf(
                                                "[STEP1][END] plyNo=%s depSeq=%d factor=%.12f balance=%,.2f%n",
                                                plyNo,
                                                dep.getDepositSeq(),
                                                factor,
                                                balance);
                        }

                        // ===== Detail =====
                        Step1Detail d = new Step1Detail();
                        d.plyNo = plyNo;
                        d.depositSeq = dep.getDepositSeq();
                        d.depositDate = dep.getDepositDate();
                        d.principal = dep.getPrincipal();
                        d.factor = factor;
                        d.balance = balance;
                        d.step1EndDate = step1EndDate;
                        d.productCode = c.getProductCode();

                        detailList.add(d);

                        // ===== Summary =====
                        Step1Summary s = summaryMap.get(plyNo);
                        if (s == null) {
                                s = new Step1Summary();
                                s.plyNo = plyNo;
                                s.balance = 0.0;
                                s.step1EndDate = step1EndDate;
                                s.annuityDate = c.getAnnuityDate();
                                s.contractDate = c.getInsStartDate();
                                s.rateCode = c.getRateCode();
                                s.productCode = c.getProductCode();
                                s.expenseKey = c.getExpenseKey();
                                s.annuityTerm = c.getAnnuityTerm();
                                s.insEndDate = c.getInsEndDate();
                        }

                        s.balance += balance;
                        summaryMap.put(plyNo, s);
                }

                return new Result(detailList, summaryMap);
        }
}
