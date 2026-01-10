package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step2Detail;
import com.interestcalc.util.CsvUtil;

public class Step2DetailCsvWriter {

    public static void write(Path path, List<Step2Detail> list) throws Exception {

        try (BufferedWriter w = Files.newBufferedWriter(path)) {

            w.write(String.join(",",
                    "PLYNO",
                    "FROM_DATE",
                    "TO_DATE",
                    "BEGIN_BALANCE",
                    "ANNUAL_EXPENSE",
                    "BASE_AFTER_ANNUAL",
                    "FACTOR",
                    "END_BALANCE"));
            w.newLine();

            for (Step2Detail d : list) {
                w.write(String.join(",",
                        CsvUtil.s(d.plyNo),
                        CsvUtil.fmt(d.fromDate),
                        CsvUtil.fmt(d.toDate),
                        CsvUtil.fmt(d.beginBalance),
                        CsvUtil.fmt(d.annualExpense),
                        CsvUtil.fmt(d.baseAfterAnnual),
                        CsvUtil.fmt(d.factor),
                        CsvUtil.fmt(d.endBalance)));
                w.newLine();
            }
        }
    }
}
