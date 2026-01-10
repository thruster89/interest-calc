package com.interestcalc.test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateSegment;
import com.interestcalc.loader.RateCsvLoader;

public class RateLoadTest {

    public static void main(String[] args) throws Exception {

        Map<String, List<RateSegment>> rateMap = RateCsvLoader.load(Path.of("data/rate.csv"));

        // ===== 특정 RateCode 확인 =====
        List<RateSegment> r01 = rateMap.get("00010");

        System.out.println("RateCode = 00010");
        for (RateSegment seg : r01) {
            System.out.println(
                    seg.getFromDate() + " ~ " +
                            seg.getToDate() + " : " +
                            seg.getRate());
        }

        // ===== 날짜 기준 조회 테스트 =====
        LocalDate testDate = LocalDate.of(2024, 3, 1);

        double rate = r01.stream()
                .filter(s -> s.contains(testDate))
                .findFirst()
                .orElseThrow()
                .getRate();

        System.out.println("Rate @ " + testDate + " = " + rate);
    }
}
