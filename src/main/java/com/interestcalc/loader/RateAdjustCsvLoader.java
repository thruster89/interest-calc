package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateAdjustRuleType;

public class RateAdjustCsvLoader {

    public static Map<String, List<RateAdjustRule>> load(Path csvPath) throws IOException {

        Map<String, List<RateAdjustRule>> map = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {

            br.readLine(); // header skip

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] c = line.split(",", -1);

                String rateCode = c[0].trim();

                int baseRule = parseInt(c[1]);
                double baseAdj = parseDouble(c[2]);

                int subRule = parseInt(c[3]);
                double subAdj = parseDouble(c[4]);

                RateAdjustRuleType subRuleType = RateAdjustRuleType.fromCode(subRule);

                Integer yearFrom = null;
                Integer yearTo = null;

                if (subRuleType != RateAdjustRuleType.NONE) {
                    yearFrom = parseInt(c[5]);
                    yearTo = parseInt(c[6]);
                }
                RateAdjustRule rule = new RateAdjustRule(
                        rateCode,
                        RateAdjustRuleType.fromCode(baseRule),
                        baseAdj,
                        RateAdjustRuleType.fromCode(subRule),
                        subAdj,
                        yearFrom,
                        yearTo);

                map.computeIfAbsent(rateCode, k -> new ArrayList<>())
                        .add(rule);
            }
        }

        return map;
    }

    private static int parseInt(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
    }

    private static double parseDouble(String s) {
        return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s.trim());
    }
}
