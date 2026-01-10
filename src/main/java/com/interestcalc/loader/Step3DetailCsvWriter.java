package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step3Detail;
import com.interestcalc.util.CsvUtil;

public class Step3DetailCsvWriter {

    public static void write(Path out, List<Step3Detail> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header (VBA Step3_Detail 대응) =====
            bw.write(String.join(",",
                    "PLYNO",
                    "FROM_DATE",
                    "TO_DATE",
                    "BEGIN_BALANCE",
                    "INTEREST",
                    "ANN_AMOUNT",
                    "ANNUAL_EXPENSE",
                    "END_BALANCE",
                    "FACTOR",
                    "YEAR_IDX",
                    "REMAIN_YEARS",
                    "DISCOUNT_FACTOR"));
            bw.newLine();

            for (Step3Detail r : rows) {
                bw.write(String.join(",",
                        CsvUtil.s(r.plyNo()),
                        CsvUtil.d(r.fromDate()),
                        CsvUtil.d(r.toDate()),
                        CsvUtil.n(r.beginBalance()),
                        CsvUtil.n(r.interest()),
                        CsvUtil.n(r.annAmount()),
                        CsvUtil.n(r.annualExpense()),
                        CsvUtil.n(r.endBalance()),
                        CsvUtil.n(r.factor()),
                        CsvUtil.i(r.yearIdx()),
                        CsvUtil.i(r.remainYears()),
                        CsvUtil.n(r.discountFactor())));
                bw.newLine();
            }
        }
    }
}
