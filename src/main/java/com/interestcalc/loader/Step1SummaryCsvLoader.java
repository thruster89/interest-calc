package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.interestcalc.domain.Step1Summary;
import com.interestcalc.util.CsvUtil;

public class Step1SummaryCsvLoader {

    public static List<Step1Summary> load(Path csv) throws IOException {

        List<Step1Summary> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csv)) {

            // header skip
            String header = br.readLine();
            if (header == null) {
                return out;
            }

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] c = line.split(",", -1);
                int k = 0;

                Step1Summary s = Step1Summary.fromCsv(
                        c[k++].trim(), // PLYNO
                        CsvUtil.parseDouble(c[k++]), // BALANCE (net)
                        CsvUtil.parseDate(c[k++]), // PAY_END_DATE
                        CsvUtil.parseDate(c[k++]), // ANNUITY_DATE
                        CsvUtil.parseDate(c[k++]), // INS_ST
                        c[k++].trim(), // RATE_CODE
                        c[k++].trim(), // GDCD
                        c[k++].trim(), // PR_BZCS_DSCNO
                        CsvUtil.parseInt(c[k++]), // AN_PY_TRM
                        CsvUtil.parseDate(c[k++]), // INS_CLSTR
                        CsvUtil.parseDouble(c[k++]), // TOTAL_BALANCE
                        CsvUtil.parseDouble(c[k++]) // DED_AMT_LAST
                );

                out.add(s);
            }
        }

        return out;
    }
}
