package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step2Summary;

public class Step2SummaryCsvWriter {

    public static void write(Path path, List<Step2Summary> list) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            bw.write(
                    "PLYNO,BALANCE,Calc_base_Date,ANNUITY_DATE," +
                            "RATE_CODE,GDCD,PR_BZCS_DSCNO,AN_PY_TRM,INS_CLSTR,INS_ST");
            bw.newLine();

            for (Step2Summary s : list) {
                bw.write(String.join(",",
                        s.plyNo,
                        BigDecimal.valueOf(s.balance)
                                .stripTrailingZeros()
                                .toPlainString(),
                        s.calcBaseDate.toString(),
                        s.annuityDate.toString(),
                        s.rateCode,
                        s.productCode,
                        s.expenseKey,
                        String.valueOf(s.annuityTerm),
                        s.insClusterDate.toString(),
                        s.insStartDate.toString()));
                bw.newLine();
            }
        }
    }
}
