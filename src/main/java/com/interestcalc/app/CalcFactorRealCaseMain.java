package com.interestcalc.app;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.interestcalc.calc.CalcFactorByYear;
import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.Contract;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.loader.ContractCsvLoader;
import com.interestcalc.loader.DepositCsvLoader;
import com.interestcalc.loader.MinGuaranteedRateCsvLoader;
import com.interestcalc.loader.RateAdjustCsvLoader;
import com.interestcalc.loader.RateCsvLoader;

public class CalcFactorRealCaseMain {

    public static void main(String[] args) throws Exception {

        // ===============================
        // 1. CSV 경로 (VBA랑 동일한 파일)
        // ===============================
        Path base = Path.of("data"); // ← 네 CSV 폴더

        Map<String, Contract> contractMap = ContractCsvLoader.load(base.resolve("Contract.csv"));

        Map<String, List<RateSegment>> rateMap = RateCsvLoader.load(base.resolve("Rate.csv"));

        Map<String, List<MinGuaranteedRateSegment>> mgrMap = MinGuaranteedRateCsvLoader
                .load(base.resolve("min_guaranteed_rate.csv"));

        Map<String, List<RateAdjustRule>> rateAdjustMap = RateAdjustCsvLoader.load(base.resolve("RateAdjust.csv"));

        List<Deposit> deposits = DepositCsvLoader.load(base.resolve("Deposit.csv"));

        // ===============================
        // 2. 테스트할 계약 1건 선택
        // ===============================
        String testPlyNo = "282120090000065"; // ← VBA에서 쓰는 실제 값

        Contract c = contractMap.get(testPlyNo);
        if (c == null) {
            throw new IllegalStateException("Contract not found: " + testPlyNo);
        }

        Deposit dep = deposits.stream()
                .filter(d -> d.getPlyNo().equals(testPlyNo))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Deposit not found"));

        // ===============================
        // 3. CalcContext 구성 (VBA와 1:1)
        // ===============================
        CalcContext ctx = new CalcContext();
        ctx.plyNo = testPlyNo;
        ctx.depositSeq = dep.getDepositSeq();
        ctx.contractDate = c.getInsStartDate();
        ctx.principal = dep.getPrincipal();

        ctx.rateArr = rateMap.get(c.getRateCode());
        ctx.mgrArr = mgrMap.get(c.getProductCode());
        ctx.rateAdjustRules = rateAdjustMap.get(c.getRateCode());

        ctx.debugMode = true;
        ctx.applyTag = "STEP1";

        // ===============================
        // 4. Step1 종료일 (VBA와 동일)
        // ===============================
        LocalDate payEndDate = c.getInsStartDate().plusYears(c.getPayYears());

        LocalDate calcBaseDate = LocalDate.of(2025, 12, 31); // ← Run 시트 값 그대로

        LocalDate step1EndDate = calcBaseDate.isBefore(payEndDate) ? calcBaseDate : payEndDate;

        // ===============================
        // 5. CalcFactorByYear 실행
        // ===============================
        double factor = CalcFactorByYear.calc(
                ctx,
                dep.getDepositDate(),
                step1EndDate);

        double balance = dep.getPrincipal() * factor;

        // ===============================
        // 6. 결과 출력
        // ===============================
        System.out.println("===== REAL CASE TEST =====");
        System.out.println("PLY_NO        = " + testPlyNo);
        System.out.println("DEPOSIT_SEQ   = " + dep.getDepositSeq());
        System.out.println("FROM          = " + dep.getDepositDate());
        System.out.println("TO            = " + step1EndDate);
        System.out.println("PRINCIPAL     = " + dep.getPrincipal());
        System.out.println("FACTOR        = " + factor);
        System.out.println("BALANCE       = " + balance);
    }
}
