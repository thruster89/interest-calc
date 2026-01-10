package com.interestcalc.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.interestcalc.context.CalcBaseDateType;
import com.interestcalc.context.CalcMasterData;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.loader.*;
import com.interestcalc.service.*;

@Component
public class InterestCalcMain {

        private static final Logger log = LoggerFactory.getLogger(InterestCalcMain.class);

        public void run() throws Exception {

                long startTime = System.currentTimeMillis();
                log.info("Interest calculation started");

                // ====================================
                // Run params (VBA Run 시트 대응)
                // ====================================
                CalcRunContext runCtx = new CalcRunContext(
                                "ALL",
                                "282120090000067",
                                CalcBaseDateType.CONTRACT,
                                LocalDate.of(2025, 12, 31),
                                2025,
                                true);

                // ==================================================
                // Paths
                // ==================================================
                Path outDir = Path.of("out");
                Files.createDirectories(outDir);

                Path dataDir = Path.of("data");

                // ====================================
                // Load master data
                // ====================================
                log.info("Loading master data");
                CalcMasterData master = CalcMasterData.loadAll(dataDir);

                // ====================================
                // Load deposits
                // ====================================
                log.info("Loading deposit data");
                List<Deposit> deposits = DepositCsvLoader.load(dataDir.resolve("deposit.csv"));

                // ==================================================
                // STEP 1
                // ==================================================
                log.info("STEP1 started");
                Step1Service step1Service = new Step1Service(
                                master.contractMap,
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap);

                Step1Service.Result step1 = step1Service.run(runCtx, deposits);

                List<Step1Summary> step1Summaries = List.copyOf(step1.summaries().values());

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

                log.info("STEP1 finished: summaries={}", step1Summaries.size());

                // ==================================================
                // STEP 2
                // ==================================================
                log.info("STEP2 started");

                Step2Service step2Service = new Step2Service(
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap,
                                master.expenseMap);

                Step2Service.Result step2 = step2Service.run(runCtx, step1Summaries);

                List<Step2Summary> step2Summaries = step2.summaries();

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

                log.info("STEP2 finished: summaries={}", step2Summaries.size());

                // ==================================================
                // STEP 3
                // ==================================================
                log.info("STEP3 started");

                Step3Service step3Service = new Step3Service(
                                master.rateMap,
                                master.mgrMap,
                                master.rateAdjustMap,
                                master.expenseMap);

                Step3Service.Result step3 = step3Service.run(runCtx, step2Summaries);

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

                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Interest calculation finished. elapsed={} ms", elapsed);
        }
}
