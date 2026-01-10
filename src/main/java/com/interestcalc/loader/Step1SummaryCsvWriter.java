package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step1Summary;
import com.interestcalc.util.CsvUtil;

public class Step1SummaryCsvWriter {

    public static void write(Path out, List<Step1Summary> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header =====
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

                String netBalanceStr = BigDecimal.valueOf(r.netBalance).toPlainString();
                String totalBalanceStr = BigDecimal.valueOf(r.totalBalance).toPlainString();
                String dedAmtLastStr = BigDecimal.valueOf(r.dedAmtLast).toPlainString();

                bw.write(String.join(",",
                        CsvUtil.s(r.plyNo),
                        netBalanceStr,
                        CsvUtil.d(r.step1EndDate),
                        CsvUtil.d(r.annuityDate),
                        CsvUtil.d(r.contractDate),
                        CsvUtil.s(r.rateCode),
                        CsvUtil.s(r.productCode),
                        CsvUtil.s(r.expenseKey),
                        CsvUtil.i(r.annuityTerm),
                        CsvUtil.d(r.insEndDate),
                        totalBalanceStr,
                        dedAmtLastStr));
                bw.newLine();
            }
        }
    }
}
