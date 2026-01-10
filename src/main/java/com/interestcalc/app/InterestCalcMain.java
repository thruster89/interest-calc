package com.interestcalc.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import com.interestcalc.context.CalcBaseDateType;
import com.interestcalc.context.CalcMasterData;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.loader.CalcDebugCsvWriter;
import com.interestcalc.loader.DepositCsvLoader;
import com.interestcalc.loader.Step1DetailCsvWriter;
import com.interestcalc.loader.Step1SummaryCsvWriter;
import com.interestcalc.loader.Step2DetailCsvWriter;
import com.interestcalc.loader.Step2ExpDetailCsvWriter;
import com.interestcalc.loader.Step2SummaryCsvWriter;
import com.interestcalc.loader.Step3DetailCsvWriter;
import com.interestcalc.loader.Step3SummaryCsvWriter;
import com.interestcalc.service.Step1Service;
import com.interestcalc.service.Step2Service;
import com.interestcalc.service.Step3Service;

public class InterestCalcMain {

        public static void main(String[] args) throws Exception {

                // ====================================
                // Run params (VBA Run 시트 대응)
                // ====================================
                CalcRunContext runCtx = new CalcRunContext(
                                "ALL", // runMode: ALL | ONE
                                "282120090000067", // targetPlyNo
                                CalcBaseDateType.CONTRACT, // FIXED | CONTRACT
                                LocalDate.of(2025, 12, 31), // fixedBaseDate
                                2025, // contractYear (CONTRACT일 때만 의미)
                                true // debugMode
                );
                // ==================================================
                // Paths
                // ==================================================
                Path outDir = Path.of("out");
                Files.createDirectories(outDir);
                // ====================================
                // Load master data (한 번만)
                // ====================================
                Path dataDir = Path.of("data");
                CalcMasterData master = CalcMasterData.loadAll(dataDir);

                // ====================================
                // Load deposits
                // ====================================
                List<Deposit> deposits = DepositCsvLoader.load(dataDir.resolve("deposit.csv"));

                // ==================================================
                // STEP 1
                // ==================================================
                Step1Service step1Service = new Step1Service(
                                master.contractMap,
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap);

                Step1Service.Result step1 = step1Service.run(runCtx, deposits);

                List<Step1Summary> step1Summaries = List.copyOf(step1.summaries().values());

                // ---- OUTPUT STEP1
                Step1DetailCsvWriter.write(
                                outDir.resolve("step1_detail.csv"),
                                step1.details());

                Step1SummaryCsvWriter.write(
                                outDir.resolve("step1_summary.csv"),
                                step1Summaries);

                if (runCtx.debugMode) {
                        CalcDebugCsvWriter.write(
                                        outDir.resolve("step1_debug.csv"),
                                        step1.debugRows());
                }

                // ==================================================
                // STEP 2
                // ==================================================
                Step2Service step2Service = new Step2Service(
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap,
                                master.expenseMap);

                Step2Service.Result step2 = step2Service.run(runCtx, step1Summaries);

                List<Step2Summary> step2Summaries = step2.summaries();

                // ---- OUTPUT STEP2
                Step2DetailCsvWriter.write(
                                outDir.resolve("step2_detail.csv"),
                                step2.details());

                Step2ExpDetailCsvWriter.write(
                                outDir.resolve("step2_exp_detail.csv"),
                                step2.expDetails());

                Step2SummaryCsvWriter.write(
                                outDir.resolve("step2_summary.csv"),
                                step2Summaries);

                if (runCtx.debugMode) {
                        CalcDebugCsvWriter.write(
                                        outDir.resolve("step2_debug.csv"),
                                        step2.debugRows());
                }

                // ==================================================
                // STEP 3
                // ==================================================
                Step3Service step3Service = new Step3Service(
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap,
                                master.expenseMap);

                Step3Service.Result step3 = step3Service.run(runCtx, step2Summaries);

                // ---- OUTPUT STEP3
                Step3DetailCsvWriter.write(
                                outDir.resolve("step3_detail.csv"),
                                step3.details());

                Step3SummaryCsvWriter.write(
                                outDir.resolve("step3_summary.csv"),
                                step3.summaries());

                if (runCtx.debugMode) {
                        CalcDebugCsvWriter.write(
                                        outDir.resolve("step3_debug.csv"),
                                        step3.debugRows());
                }

                // ==================================================
                System.out.println("=== INTEREST CALC COMPLETE (STEP1~3) ===");
        }
}