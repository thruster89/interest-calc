package com.interestcalc.test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.loader.MinGuaranteedRateCsvLoader;

public class MinGuaranteedRateLoadTest {

    public static void main(String[] args) throws Exception {

        Map<String, List<MinGuaranteedRateSegment>> mgrMap = MinGuaranteedRateCsvLoader.load(
                Path.of("data/min_guaranteed_rate.csv"));

        List<MinGuaranteedRateSegment> segs = mgrMap.get("LA02821001");

        System.out.println("ProductCode = LA02821001");
        for (MinGuaranteedRateSegment seg : segs) {
            System.out.println(
                    seg.getYearFrom() + " ~ " +
                            seg.getYearTo() + " : " +
                            seg.getRate());
        }
    }
}
