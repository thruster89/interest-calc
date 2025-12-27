package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.interestcalc.domain.Step1Summary;

public class Step1SummaryCsvWriter {

    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void write(Path path, List<Step1Summary> list) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            // ===== Header (VBA Step1_Summary 기준) =====
            bw.write(String.join(",",
                    "PLYNO",
                    "BALANCE",
                    "PAY_END_DATE",
                    "ANNUITY_DATE",
                    "INS_ST",
                    "INS_CLSTR",
                    "RATE_CODE",
                    "GDCD",
                    "PR_BZCS_DSCNO",
                    "AN_PY_TRM",
                    "INS_CLSTR"));
            bw.newLine();

            for (Step1Summary s : list) {

                bw.write(String.join(",",
                        s.plyNo,
                        fmt(s.balance),
                        fmt(s.step1EndDate),
                        fmt(s.annuityDate),
                        fmt(s.contractDate),
                        fmt(s.insEndDate),
                        s.rateCode,
                        s.productCode,
                        s.expenseKey,
                        String.valueOf(s.annuityTerm),
                        fmt(s.insEndDate)));
                bw.newLine();
            }
        }
    }

    private static String fmt(Object v) {
        if (v == null)
            return "";
        if (v instanceof Double aDouble)
            return String.format("%.10f", aDouble);
        if (v instanceof java.time.LocalDate localDate)
            return localDate.format(DF);
        return v.toString();
    }
}
