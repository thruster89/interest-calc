package com.interestcalc.test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.loader.RateAdjustCsvLoader;

public class TestRateAdjustLoader {

    public static void main(String[] args) throws Exception {

        Path csvPath = Path.of("data/RateAdjust.csv");

        Map<String, List<RateAdjustRule>> map = RateAdjustCsvLoader.load(csvPath);

        for (String rateCode : map.keySet()) {

            System.out.println("====================================");
            System.out.println("RATE_CODE = " + rateCode);

            List<RateAdjustRule> rules = map.get(rateCode);

            for (int i = 0; i < rules.size(); i++) {
                RateAdjustRule r = rules.get(i);

                System.out.printf(
                        "  [%d] BaseRule=%s adj=%.4f | SubRule=%s subAdj=%.4f | Year=%s~%s%n",
                        i,
                        r.getBaseRule(),
                        r.getBaseAdj(),
                        r.getSubRule(),
                        r.getSubAdj(),
                        r.getYearFrom(),
                        r.getYearTo());
            }
        }
    }
}
