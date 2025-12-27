package com.interestcalc.loader;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.interestcalc.domain.Expense;

public class ExpenseCsvLoader {

    public static Map<String, Expense> load(Path path) throws Exception {

        Map<String, Expense> map = new HashMap<>();

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

                String key = t[0].trim();
                double monthlyAmt = Double.parseDouble(t[1].trim());
                double yearlyRate = Double.parseDouble(t[2].trim());
                double ayamtRate = Double.parseDouble(t[3].trim());

                map.put(key, new Expense(key, monthlyAmt, yearlyRate, ayamtRate));
            }
        }
        return map;
    }
}
