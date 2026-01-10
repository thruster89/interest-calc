package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step1Summary;
import com.interestcalc.util.CsvUtil;

public class Step1SummaryCsvWriter {

    public static void write(Path out, List<Step1Summary> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header (VBA Step1_Summary 동일) =====
            bw.write(String.join(",",
                    "PLYNO",
                    "NET_BALANCE",
                    "STEP1_END_DATE",
                    "ANNUITY_DATE",
                    "CONTRACT_DATE",
                    "RATE_CODE",
                    "PRODUCT_CODE",
                    "EXPENSE_KEY",
                    "ANNUITY_TERM",
                    "INS_END_DATE",
                    "TOTAL_BALANCE",
                    "DED_AMT_LAST"));
            bw.newLine();

            // ===== Rows =====
            for (Step1Summary r : rows) {
                bw.write(String.join(",",
                        CsvUtil.s(r.plyNo),
                        CsvUtil.n(r.netBalance),
                        CsvUtil.d(r.step1EndDate),
                        CsvUtil.d(r.annuityDate),
                        CsvUtil.d(r.contractDate),
                        CsvUtil.s(r.rateCode),
                        CsvUtil.s(r.productCode),
                        CsvUtil.s(r.expenseKey),
                        CsvUtil.i(r.annuityTerm),
                        CsvUtil.d(r.insEndDate),
                        CsvUtil.n(r.totalBalance),
                        CsvUtil.n(r.dedAmtLast)));
                bw.newLine();
            }
        }
    }
}
