package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step3Detail;

public class Step3DetailCsvWriter {

    public static void write(Path path, List<Step3Detail> list) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            bw.write("PLYNO,FROM_DATE,TO_DATE,BEGIN_BAL,ANN_AMT,ANNUAL_EXP,INTEREST,END_BAL,FACTOR");
            bw.newLine();

            for (Step3Detail d : list) {
                bw.write(String.join(",",
                        d.plyNo,
                        d.fromDate.toString(),
                        d.toDate.toString(),
                        bd(d.beginBalance),
                        bd(d.annuityAmount),
                        bd(d.annualExpense),
                        bd(d.interest),
                        bd(d.endBalance),
                        bd(d.factor)));
                bw.newLine();
            }
        }
    }

    private static String bd(double v) {
        return BigDecimal.valueOf(v)
                .stripTrailingZeros()
                .toPlainString();
    }
}
