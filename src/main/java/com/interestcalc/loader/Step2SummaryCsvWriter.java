package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step2Summary;
import com.interestcalc.util.CsvUtil;

public class Step2SummaryCsvWriter {

    public static void write(Path path, List<Step2Summary> list) throws Exception {

        try (BufferedWriter w = Files.newBufferedWriter(path)) {

            // ===== Header =====
            w.write(String.join(",",
                    "PLYNO",
                    "BALANCE",
                    "STEP2_END_DATE",
                    "ANNUITY_DATE",
                    "RATE_CODE",
                    "PRODUCT_CODE",
                    "EXPENSE_KEY",
                    "ANNUITY_TERM",
                    "INS_END_DATE",
                    "CONTRACT_DATE",
                    "BASE_END_BALANCE",
                    "TOTAL_MONTHLY_EXP_ACC"));
            w.newLine();

            // ===== Rows =====
            for (Step2Summary s : list) {
                w.write(String.join(",",
                        s.plyNo,
                        CsvUtil.n(s.balance),
                        CsvUtil.d(s.step2EndDate),
                        CsvUtil.d(s.annuityDate),
                        CsvUtil.s(s.rateCode),
                        CsvUtil.s(s.productCode),
                        CsvUtil.s(s.expenseKey),
                        CsvUtil.i(s.annuityTerm),
                        CsvUtil.d(s.insEndDate),
                        CsvUtil.d(s.contractDate),
                        CsvUtil.n(s.baseEndBalance),
                        CsvUtil.n(s.totalMonthlyExpenseAcc)));
                w.newLine();
            }
        }
    }
}
