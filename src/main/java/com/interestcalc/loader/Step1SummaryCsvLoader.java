package com.interestcalc.loader;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.interestcalc.domain.Step1Summary;

public class Step1SummaryCsvLoader {

    public static List<Step1Summary> load(Path path) throws Exception {

        List<Step1Summary> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {

                if (header) {
                    header = false;
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;

                String[] t = line.split(",", -1);

                Step1Summary s = new Step1Summary();
                s.plyNo = t[0].trim();
                s.balance = Double.parseDouble(t[1].trim());
                s.step1EndDate = LocalDate.parse(t[2].trim());
                s.annuityDate = LocalDate.parse(t[3].trim());
                s.contractDate = LocalDate.parse(t[4].trim());
                s.insEndDate = LocalDate.parse(t[5].trim());
                s.rateCode = t[6].trim();
                s.productCode = t[7].trim();
                s.expenseKey = t[8].trim();
                s.annuityTerm = Long.parseLong(t[9].trim());

                list.add(s);
            }
        }
        return list;
    }
}
