package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.util.RateNormalizer;

public class MinGuaranteedRateCsvLoader {

    /**
     * @return Map<PRODUCT_CODE, List<MinGuaranteedRateSegment>>
     */
    public static Map<String, List<MinGuaranteedRateSegment>> load(Path csvPath) throws IOException {

        Map<String, List<MinGuaranteedRateSegment>> map = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] t = line.split(",", -1);

                String productCode = t[0].trim();
                int yearFrom = Integer.parseInt(t[1].trim());
                int yearTo = Integer.parseInt(t[2].trim());
                double rawRate = Double.parseDouble(t[3].trim());
                double rate = RateNormalizer.normalize(rawRate);

                MinGuaranteedRateSegment seg = new MinGuaranteedRateSegment(yearFrom, yearTo, rate);

                map.computeIfAbsent(productCode, k -> new ArrayList<>())
                        .add(seg);
            }
        }

        return map;
    }
}
