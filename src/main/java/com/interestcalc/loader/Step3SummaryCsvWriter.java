package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step3Summary;
import com.interestcalc.util.CsvUtil;

public class Step3SummaryCsvWriter {

    public static void write(Path out, List<Step3Summary> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header =====
            bw.write(String.join(",",
                    "PLYNO",
                    "BALANCE",
                    "CALC_BASE_DATE",
                    "RATE_CODE",
                    "PRODUCT_CODE",
                    "EXPENSE_KEY",
                    "ANNUITY_TERM"));
            bw.newLine();

            for (Step3Summary r : rows) {

                // 🔥 핵심: balance는 BigDecimal → toPlainString
                String balanceStr = BigDecimal
                        .valueOf(r.balance())
                        .toPlainString();

                bw.write(String.join(",",
                        CsvUtil.s(r.plyNo()),
                        balanceStr,
                        CsvUtil.d(r.calcBaseDate()),
                        CsvUtil.s(r.rateCode()),
                        CsvUtil.s(r.productCode()),
                        CsvUtil.s(r.expenseKey()),
                        CsvUtil.i(r.annuityTerm())));
                bw.newLine();
            }
        }
    }
}
