package com.interestcalc.loader;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateAdjustRule;

/**
 * RATE_ADJUST CSV Loader
 * BaseRule + (선택) SubRule 구조
 */
public class RateAdjustCsvLoader {

    public static Map<String, List<RateAdjustRule>> load(Path path) throws Exception {

        Map<String, List<RateAdjustRule>> map = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {

            br.readLine(); // header skip

            String line;
            while ((line = br.readLine()) != null) {

                if (line.isBlank())
                    continue;

                String[] c = line.split(",");

                String rateCode = c[0].trim();

                int baseRule = Integer.parseInt(c[1].trim());
                double baseAdj = Double.parseDouble(c[2].trim());

                Integer subRule = null;
                Double subAdj = null;
                Integer yearFrom = null;
                Integer yearTo = null;

                // SubRule 존재 시만 처리
                if (c.length >= 7 && !c[3].isBlank()) {
                    subRule = Integer.valueOf(c[3].trim());
                    subAdj = Double.valueOf(c[4].trim());
                    yearFrom = Integer.valueOf(c[5].trim());
                    yearTo = Integer.valueOf(c[6].trim());
                }

                RateAdjustRule rule = new RateAdjustRule(
                        rateCode,
                        baseRule,
                        baseAdj,
                        subRule,
                        subAdj,
                        yearFrom,
                        yearTo);

                map.computeIfAbsent(rateCode, k -> new ArrayList<>())
                        .add(rule);
            }
        }
        return map;
    }
}
