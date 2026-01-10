package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.util.CsvUtil;

public class CalcDebugCsvWriter {

    public static void write(Path out, List<CalcDebugRow> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header (VBA wsDebug 대응) =====
            bw.write(String.join(",",
                    "TAG",
                    "APPLY_TAG",
                    "PLYNO",
                    "DEPOSIT_SEQ",
                    "YEAR_IDX",
                    "ELAPSED_YEARS",
                    "FROM_DATE",
                    "TO_DATE",
                    "DAYS",
                    "BASE_RATE",
                    "RATE_ADD",
                    "RATE_MUL",
                    "ADJ_RATE",
                    "MGR_RATE",
                    "APPLIED_RATE",
                    "ACC",
                    "YEAR_SUM",
                    "PRINCIPAL",
                    "FACTOR",
                    "BALANCE"));
            bw.newLine();

            // ===== Rows =====
            for (CalcDebugRow r : rows) {
                bw.write(String.join(",",
                        CsvUtil.s(r.tag()),
                        CsvUtil.s(r.applyTag()),
                        CsvUtil.s(r.plyNo()),
                        CsvUtil.l(r.depositSeq()),
                        CsvUtil.i(r.yearIdx()),
                        CsvUtil.i(r.elapsedYears()),
                        CsvUtil.d(r.fromDate()),
                        CsvUtil.d(r.toDate()),
                        CsvUtil.i(r.days()),
                        CsvUtil.n(r.baseRate()),
                        CsvUtil.n(r.rateAdd()),
                        CsvUtil.n(r.rateMul()),
                        CsvUtil.n(r.adjRate()),
                        CsvUtil.n(r.mgrRate()),
                        CsvUtil.n(r.appliedRate()),
                        CsvUtil.n(r.acc()),
                        CsvUtil.n(r.yearSum()),
                        CsvUtil.n(r.principal()),
                        CsvUtil.n(r.factor()),
                        CsvUtil.n(r.balance())));
                bw.newLine();
            }
        }
    }
}
