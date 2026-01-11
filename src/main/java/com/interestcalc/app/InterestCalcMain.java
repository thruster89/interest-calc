// package com.interestcalc.app;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.ObjectProvider;
// import org.springframework.stereotype.Component;

// import com.interestcalc.context.CalcRunContext;
// import com.interestcalc.domain.Deposit;
// import com.interestcalc.domain.Step1Summary;
// import com.interestcalc.domain.Step2Summary;
// import com.interestcalc.loader.CalcDebugCsvWriter;
// import com.interestcalc.loader.Step1DetailCsvWriter;
// import com.interestcalc.loader.Step1SummaryCsvWriter;
// import com.interestcalc.loader.Step2DetailCsvWriter;
// import com.interestcalc.loader.Step2ExpDetailCsvWriter;
// import com.interestcalc.loader.Step2SummaryCsvWriter;
// import com.interestcalc.loader.Step3DetailCsvWriter;
// import com.interestcalc.loader.Step3SummaryCsvWriter;
// import com.interestcalc.mapper.DepositMapper;
// import com.interestcalc.service.Step1Service;
// import com.interestcalc.service.Step2Service;
// import com.interestcalc.service.Step3Service;

// @Component
// public class InterestCalcMain {

// private static final Logger log =
// LoggerFactory.getLogger(InterestCalcMain.class);

// private final Step1Service step1Service;
// private final Step2Service step2Service;
// private final Step3Service step3Service;
// private final ObjectProvider<CalcRunContext> runCtxProvider;
// private final DepositMapper depositMapper;

// public InterestCalcMain(
// DepositMapper depositMapper,
// Step1Service step1Service,
// Step2Service step2Service,
// Step3Service step3Service,
// ObjectProvider<CalcRunContext> runCtxProvider) {

// this.depositMapper = depositMapper;
// this.step1Service = step1Service;
// this.step2Service = step2Service;
// this.step3Service = step3Service;
// this.runCtxProvider = runCtxProvider;
// }

// public void run() throws Exception {

// long startTime = System.currentTimeMillis();
// log.info("Interest calculation started");

// // CalcRunContext runCtx = new CalcRunContext(
// // "ALL",
// // "282120090000066",
// // CalcBaseDateType.CONTRACT,
// // LocalDate.of(2025, 12, 31),
// // 2025,
// // true);
// CalcRunContext runCtx = runCtxProvider.getObject();

// Path outDir = Path.of("out");
// Files.createDirectories(outDir);

// // Path dataDir = Path.of("data");

// // ===== Load deposits only (master는 Spring이 관리) =====
// log.info("Loading deposit data");
// List<Deposit> deposits = depositMapper.selectDeposits(
// runCtx.runMode,
// runCtx.targetPlyNo);

// // ==================================================
// // STEP 1
// // ==================================================
// log.info("STEP1 started");

// Step1Service.Result step1 = step1Service.run(runCtx, deposits);

// List<Step1Summary> step1Summaries = List.copyOf(step1.summaries().values());

// Step1DetailCsvWriter.write(
// outDir.resolve("step1_detail.csv"),
// step1.details());

// Step1SummaryCsvWriter.write(
// outDir.resolve("step1_summary.csv"),
// step1Summaries);

// if (runCtx.debugMode) {
// CalcDebugCsvWriter.write(
// outDir.resolve("step1_debug.csv"),
// step1.debugRows());
// }

// log.info("STEP1 finished: summaries={}", step1Summaries.size());

// // ==================================================
// // STEP 2
// // ==================================================
// log.info("STEP2 started");

// Step2Service.Result step2 = step2Service.run(runCtx, step1Summaries);

// List<Step2Summary> step2Summaries = step2.summaries();

// Step2DetailCsvWriter.write(
// outDir.resolve("step2_detail.csv"),
// step2.details());

// Step2ExpDetailCsvWriter.write(
// outDir.resolve("step2_exp_detail.csv"),
// step2.expDetails());

// Step2SummaryCsvWriter.write(
// outDir.resolve("step2_summary.csv"),
// step2Summaries);

// if (runCtx.debugMode) {
// CalcDebugCsvWriter.write(
// outDir.resolve("step2_debug.csv"),
// step2.debugRows());
// }

// log.info("STEP2 finished: summaries={}", step2Summaries.size());

// // ==================================================
// // STEP 3
// // ==================================================
// log.info("STEP3 started");

// Step3Service.Result step3 = step3Service.run(runCtx, step2Summaries);

// Step3DetailCsvWriter.write(
// outDir.resolve("step3_detail.csv"),
// step3.details());

// Step3SummaryCsvWriter.write(
// outDir.resolve("step3_summary.csv"),
// step3.summaries());

// if (runCtx.debugMode) {
// CalcDebugCsvWriter.write(
// outDir.resolve("step3_debug.csv"),
// step3.debugRows());
// }
// log.info("STEP3 finished: summaries={}", step3.summaries().size());

// long elapsed = System.currentTimeMillis() - startTime;
// log.info("Interest calculation finished. elapsed={} ms", elapsed);
// }
// }
