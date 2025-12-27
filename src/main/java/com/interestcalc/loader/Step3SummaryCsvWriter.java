package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step3Summary;

public class Step3SummaryCsvWriter {

    public static void write(Path path, List<Step3Summary> list) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            bw.write("PLYNO,BALANCE,CALC_BASE_DATE,RATE_CODE,GDCD,PR_BZCS_DSCNO,AN_PY_TRM");
            bw.newLine();

            for (Step3Summary s : list) {
                bw.write(String.join(",",
                        s.plyNo,
                        bd(s.balance),
                        s.calcBaseDate.toString(),
                        s.rateCode,
                        s.productCode,
                        s.expenseKey,
                        String.valueOf(s.annuityTerm)));
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
