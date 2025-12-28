package com.interestcalc.rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RateAdjustCsvLoader {

    public List<RateAdjustRule> load(Path path) throws IOException {

        List<RateAdjustRule> rules = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.readLine(); // header skip

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

                String[] c = line.split(",");

                rules.add(new RateAdjustRule(
                        c[0].trim(),
                        Integer.parseInt(c[1].trim()),
                        Integer.parseInt(c[2].trim()),
                        Integer.parseInt(c[3].trim()),
                        Double.parseDouble(c[4].trim())));
            }
        }
        return rules;
    }
}
