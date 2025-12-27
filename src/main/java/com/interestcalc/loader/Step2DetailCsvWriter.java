package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step2Detail;

public class Step2DetailCsvWriter {

    public static void write(Path path, List<Step2Detail> list) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            bw.write("PLYNO,FROM_DATE,TO_DATE,BEGIN_BAL,ANNUAL_EXP,MONTHLY_EXP,INTEREST,END_BAL,FACTOR");
            bw.newLine();

            for (Step2Detail d : list) {
                bw.write(String.join(",",
                        d.plyNo,
                        d.fromDate.toString(),
                        d.toDate.toString(),
                        String.valueOf(d.beginBalance),
                        String.valueOf(d.annualExpense),
                        String.valueOf(d.monthlyExpense),
                        String.valueOf(d.interest),
                        String.valueOf(d.endBalance),
                        String.valueOf(d.factor)));
                bw.newLine();
            }
        }
    }
}
