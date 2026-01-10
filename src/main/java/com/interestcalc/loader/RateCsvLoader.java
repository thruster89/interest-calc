package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateSegment;
import com.interestcalc.util.RateNormalizer;

public class RateCsvLoader {

    /**
     * @return Map<RATE_CODE, List<RateSegment>>
     */
    public static Map<String, List<RateSegment>> load(Path csvPath) throws IOException {

        Map<String, List<RateSegment>> map = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] t = line.split(",", -1);

                String rateCode = t[0].trim();
                LocalDate from = LocalDate.parse(t[2].trim());
                LocalDate to = LocalDate.parse(t[3].trim());
                double rawRate = Double.parseDouble(t[4].trim());
                double rate = RateNormalizer.normalize(rawRate);

                RateSegment seg = new RateSegment(from, to, rate);

                map.computeIfAbsent(rateCode, k -> new ArrayList<>())
                        .add(seg);
            }
        }

        return map;
    }
}
