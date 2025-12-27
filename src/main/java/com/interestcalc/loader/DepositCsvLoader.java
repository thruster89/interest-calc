package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.interestcalc.domain.Deposit;

public class DepositCsvLoader {

    public static List<Deposit> load(Path csvPath) throws IOException {

        List<Deposit> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] t = line.split(",", -1);

                Deposit d = new Deposit();
                d.setPlyNo(t[0].trim());
                d.setDepositSeq(Integer.parseInt(t[1].trim()));
                d.setDepositDate(LocalDate.parse(t[2].trim()));
                d.setPrincipal(Double.parseDouble(t[3].trim()));

                list.add(d);
            }
        }

        return list;
    }
}
