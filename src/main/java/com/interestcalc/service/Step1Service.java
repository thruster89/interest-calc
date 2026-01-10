package com.interestcalc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.calc.CalcBaseDateResolver;
import com.interestcalc.calc.CalcFactorByYear;
import com.interestcalc.calc.DeductionCalculator;
import com.interestcalc.context.CalcContext;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.domain.Contract;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step1Detail;
import com.interestcalc.domain.Step1Summary;

public class Step1Service {

        private final Map<String, Contract> contractMap;
        private final Map<String, List<RateSegment>> rateMap;
        private final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
        private final Map<String, List<RateAdjustRule>> rateAdjustMap;

        public Step1Service(
                        Map<String, Contract> contractMap,
                        Map<String, List<RateSegment>> rateMap,
                        Map<String, List<MinGuaranteedRateSegment>> mgrMap,
                        Map<String, List<RateAdjustRule>> rateAdjustMap) {

                this.contractMap = contractMap;
                this.rateMap = rateMap;
                this.mgrMap = mgrMap;
                this.rateAdjustMap = rateAdjustMap;
        }

        public record Result(
                        List<Step1Detail> details,
                        Map<String, Step1Summary> summaries,
                        List<CalcDebugRow> debugRows) {
        }

        public Result run(
                        CalcRunContext runCtx,
                        List<Deposit> allDeposits) {

                // ===== DepositMap (VBA BuildDepositMap) =====
                Map<String, List<Deposit>> depositMap = new LinkedHashMap<>();
                for (Deposit d : allDeposits) {
                        depositMap
                                        .computeIfAbsent(d.getPlyNo(), k -> new ArrayList<>())
                                        .add(d);
                }

                List<CalcDebugRow> debugRows = runCtx.debugMode ? new ArrayList<>() : List.of();
                List<Step1Detail> details = new ArrayList<>();
                Map<String, Step1Summary> summaries = new LinkedHashMap<>();

                // ===== Contract loop (VBA For Each plyNo In contractMap.Keys) =====
                for (Contract c : contractMap.values()) {

                        String plyNo = c.getPlyNo();

                        // 실행모드 필터
                        if ("ONE".equals(runCtx.runMode)
                                        && !plyNo.equals(runCtx.targetPlyNo)) {
                                continue;
                        }

                        // 입금 없는 계약 skip
                        List<Deposit> deps = depositMap.get(plyNo);
                        if (deps == null || deps.isEmpty()) {
                                continue;
                        }

                        // ===== CalcBaseDate =====
                        LocalDate baseDate = CalcBaseDateResolver.resolve(runCtx, c.getInsStartDate());

                        LocalDate payEndDate = c.getInsStartDate().plusYears(c.getPayYears());

                        LocalDate step1EndDate = baseDate.isBefore(payEndDate) ? baseDate : payEndDate;

                        // ===== 계약 단위 누적 변수 (VBA 그대로) =====
                        double totalBalance = 0.0;
                        double lastCalcDeductAmt = 0.0;

                        // ===== Deposit sub-loop =====
                        for (Deposit dep : deps) {

                                if (dep.getDepositDate().isAfter(step1EndDate)) {
                                        continue;
                                }

                                // ===== CalcContext =====
                                CalcContext ctx = new CalcContext();
                                ctx.plyNo = plyNo;
                                ctx.contractDate = c.getInsStartDate();
                                ctx.depositSeq = dep.getDepositSeq();
                                ctx.principal = dep.getPrincipal();
                                ctx.rateArr = rateMap.get(c.getRateCode());
                                ctx.mgrArr = mgrMap.get(c.getProductCode());
                                ctx.rateAdjustRules = rateAdjustMap.get(c.getRateCode());
                                ctx.debugMode = runCtx.debugMode;
                                ctx.applyTag = "STEP1";
                                if (ctx.debugMode) {
                                        ctx.debugRows = new ArrayList<>();
                                }

                                double factor = CalcFactorByYear.calc(
                                                ctx,
                                                dep.getDepositDate(),
                                                step1EndDate);

                                double balance = dep.getPrincipal() * factor;
                                totalBalance += balance;

                                double dedAmt = DeductionCalculator.calc(
                                                c,
                                                dep.getDepositSeq());

                                lastCalcDeductAmt = dedAmt;

                                // ===== Detail =====
                                details.add(new Step1Detail(
                                                plyNo,
                                                dep.getDepositSeq(),
                                                dep.getDepositDate(),
                                                dep.getPrincipal(),
                                                factor,
                                                balance,
                                                step1EndDate,
                                                dedAmt));
                        }

                        // ===== Summary (계약 1줄) =====
                        double netBalance = totalBalance - lastCalcDeductAmt;

                        Step1Summary summary = Step1Summary.fromContract(c, step1EndDate);

                        summary.setTotalBalance(totalBalance);
                        summary.setDeductAmount(lastCalcDeductAmt);
                        summary.setNetBalance(netBalance);

                        summaries.put(plyNo, summary);
                }

                return new Result(details, summaries, debugRows);
        }
}
