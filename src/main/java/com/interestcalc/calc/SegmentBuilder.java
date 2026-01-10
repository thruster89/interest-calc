package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;

/**
 * VBA modSegmentBuilder 이식
 *
 * 역할:
 * - RateAdjust / MGR 기준 경과연 컷 수집
 * - contractDate 기준 yyyy 경계일 생성
 * - from/to + 컷 병합 후 정렬
 * - [from, to] 기간을 sub-segment로 분할
 */
public class SegmentBuilder {

    // =====================================================
    // 내부 DateSegment (from ~ to)
    // =====================================================
    public static class DateSegment {
        public final LocalDate fromDate;
        public final LocalDate toDate;

        public DateSegment(LocalDate fromDate, LocalDate toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }
    }

    // =====================================================
    // 1. CollectElapsedYearCuts
    // VBA: CollectElapsedYearCuts
    // =====================================================
    public static Set<Integer> collectElapsedYearCuts(
            List<RateAdjustRule> rateAdjustRules,
            List<MinGuaranteedRateSegment> mgrArr) {

        Set<Integer> yearSet = new LinkedHashSet<>();

        // ---- RateAdjust ----
        if (rateAdjustRules != null) {
            for (RateAdjustRule r : rateAdjustRules) {
                if (r.hasYearRange()) {
                    yearSet.add(r.getYearFrom());
                }
            }
        }

        // ---- MGR ----
        if (mgrArr != null) {
            for (MinGuaranteedRateSegment seg : mgrArr) {
                yearSet.add(seg.getYearFrom());
            }
        }

        return yearSet;
    }

    // =====================================================
    // 2. BuildElapsedYearCuts
    // VBA: BuildElapsedYearCuts
    // =====================================================
    public static List<LocalDate> buildElapsedYearCuts(
            LocalDate contractDate,
            Set<Integer> yearSet,
            LocalDate fromDate,
            LocalDate toDate) {

        List<LocalDate> cuts = new ArrayList<>();

        for (Integer y : yearSet) {
            LocalDate cutDate = contractDate.plusYears(y);
            if (cutDate.isAfter(fromDate) && cutDate.isBefore(toDate)) {
                cuts.add(cutDate);
            }
        }

        return cuts;
    }

    // =====================================================
    // 3. MergeAndSortCuts
    // VBA: MergeAndSortCuts
    // =====================================================
    public static List<LocalDate> mergeAndSortCuts(
            LocalDate fromDate,
            LocalDate toDate,
            List<LocalDate> extraCuts) {

        Set<LocalDate> set = new LinkedHashSet<>();

        set.add(fromDate);
        set.add(toDate);

        if (extraCuts != null) {
            set.addAll(extraCuts);
        }

        List<LocalDate> res = new ArrayList<>(set);
        Collections.sort(res);

        return res;
    }

    // =====================================================
    // 4. SplitPeriodByCuts
    // VBA: SplitPeriodByCuts
    // =====================================================
    public static List<DateSegment> splitPeriodByCuts(
            List<LocalDate> cuts) {

        List<DateSegment> segs = new ArrayList<>();

        for (int i = 0; i < cuts.size() - 1; i++) {

            LocalDate from = cuts.get(i);
            LocalDate to;

            if (i < cuts.size() - 2) {
                // 다음 컷의 전날까지
                to = cuts.get(i + 1).minusDays(1);
            } else {
                // 마지막 구간은 그대로
                to = cuts.get(i + 1);
            }

            if (!from.isAfter(to)) {
                segs.add(new DateSegment(from, to));
            }
        }

        return segs;
    }
}
