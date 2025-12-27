package com.interestcalc.app;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.interestcalc.context.CalcBaseDateType;
import com.interestcalc.domain.Contract;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.Expense;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.loader.ContractCsvLoader;
import com.interestcalc.loader.DepositCsvLoader;
import com.interestcalc.loader.ExpenseCsvLoader;
import com.interestcalc.loader.MinGuaranteedRateCsvLoader;
import com.interestcalc.loader.RateCsvLoader;
import com.interestcalc.loader.Step1SummaryCsvLoader;
import com.interestcalc.loader.Step1SummaryCsvWriter;
import com.interestcalc.loader.Step2DetailCsvWriter;
import com.interestcalc.loader.Step2SummaryCsvWriter;
import com.interestcalc.loader.Step3DetailCsvWriter;
import com.interestcalc.loader.Step3SummaryCsvWriter;
import com.interestcalc.service.Step1Service;
import com.interestcalc.service.Step2Service;
import com.interestcalc.service.Step3Service;

public class Step1Main {

    public static void main(String[] args) throws Exception {

        // =====================================================
        // Run params (VBA Run 시트 대응)
        // =====================================================
        String runMode = "ALL"; // ALL | ONE
        String targetPlyNo = "282120090000065"; // ONE일 때만 사용
        CalcBaseDateType baseDateType = CalcBaseDateType.FIXED_DATE; // CONTRACT_MMDD | FIXED_DATE
        int CONTRACT_YEAR = 2025;
        LocalDate FIXED_DATE = LocalDate.of(2025, 10, 31);
        LocalDate calcBaseDate;

        if (baseDateType == CalcBaseDateType.FIXED_DATE) {
            calcBaseDate = FIXED_DATE;
        } else {
            // CONTRACT_MMDD
            calcBaseDate = LocalDate.of(CONTRACT_YEAR, 1, 1);
        }
        // LocalDate calcBaseDate = LocalDate.parse("2025-10-05");
        boolean debugMode = false;

        // =====================================================
        // Load masters (CSV)
        // =====================================================
        Map<String, Contract> contractMap = ContractCsvLoader.load(Path.of("data/contract.csv"));

        Map<String, List<RateSegment>> rateMap = RateCsvLoader.load(Path.of("data/rate.csv"));

        Map<String, List<MinGuaranteedRateSegment>> mgrMap = MinGuaranteedRateCsvLoader.load(Path.of("data/mgr.csv"));

        List<Deposit> deposits = DepositCsvLoader.load(Path.of("data/deposit.csv"));

        Map<String, Expense> expenseMap = ExpenseCsvLoader.load(Path.of("data/expense.csv"));

        // =====================================================
        // Service
        // =====================================================
        Step1Service service = new Step1Service(
                contractMap,
                rateMap,
                mgrMap,
                debugMode);

        // =====================================================
        // Execute STEP1
        // =====================================================
        Step1Service.Result result = service.run(
                deposits,
                runMode,
                targetPlyNo,
                baseDateType,
                calcBaseDate,
                CONTRACT_YEAR);

        // =====================================================
        // Result check (BALANCE ✅)
        // =====================================================
        System.out.println("===== STEP1 SUMMARY RESULT =====");
        result.summaries.values().forEach(s -> {
            System.out.printf(
                    "PLYNO=%s | BALANCE=%,.2f | END_DATE=%s%n",
                    s.plyNo,
                    s.balance,
                    s.step1EndDate);
        });
        // Step1 결과 CSV 출력
        Path step1Out = Path.of("data/step1_summary.csv");
        Step1SummaryCsvWriter.write(step1Out, new ArrayList<>(result.summaries.values()));

        System.out.println("STEP1 SUMMARY CSV WRITTEN: " + step1Out.toAbsolutePath());

        // ===============================
        // STEP2 입력 로드 (VBA Step1_Summary)
        // ===============================
        List<Step1Summary> step1Summaries = Step1SummaryCsvLoader.load(step1Out);

        // ===============================
        // STEP2 실행 ← ★ 여기 ★
        // ===============================
        Step2Service step2Service = new Step2Service();

        Step2Service.Result step2Result = step2Service.run(
                step1Summaries,
                rateMap,
                mgrMap,
                expenseMap,
                runMode,
                targetPlyNo,
                baseDateType,
                calcBaseDate,
                CONTRACT_YEAR,
                debugMode);

        // ===============================
        // STEP2 OUTPUT
        // ===============================
        Path step2DetailOut = Path.of("data/step2_detail.csv");
        Path step2SummaryOut = Path.of("data/step2_summary.csv");

        Step2DetailCsvWriter.write(step2DetailOut, step2Result.details);
        Step2SummaryCsvWriter.write(step2SummaryOut, step2Result.summaries);

        System.out.println("STEP2 DETAIL CSV WRITTEN : " + step2DetailOut);
        System.out.println("STEP2 SUMMARY CSV WRITTEN: " + step2SummaryOut);

        // ===============================
        // STEP3 실행 (연금지급기간)
        // ===============================
        Step3Service step3Service = new Step3Service();

        Step3Service.Result step3Result = step3Service.run(
                step2Result.summaries, // ★ Step2Summary 입력
                rateMap,
                mgrMap,
                expenseMap,
                baseDateType,
                calcBaseDate,
                CONTRACT_YEAR,
                debugMode);

        // ===============================
        // STEP3 OUTPUT
        // ===============================
        Path step3DetailOut = Path.of("data/step3_detail.csv");
        Path step3SummaryOut = Path.of("data/step3_summary.csv");

        Step3DetailCsvWriter.write(step3DetailOut, step3Result.details);
        Step3SummaryCsvWriter.write(step3SummaryOut, step3Result.summaries);

        System.out.println("STEP3 DETAIL CSV WRITTEN : " + step3DetailOut);
        System.out.println("STEP3 SUMMARY CSV WRITTEN: " + step3SummaryOut);
        System.out.println("===== STEP3 SUMMARY RESULT =====");
        step3Result.summaries.forEach(s -> {
            System.out.printf(
                    "PLYNO=%s | FINAL_BALANCE=%,.2f | CALC_BASE_DATE=%s%n",
                    s.plyNo,
                    s.balance,
                    s.calcBaseDate);
        });

    }

}
