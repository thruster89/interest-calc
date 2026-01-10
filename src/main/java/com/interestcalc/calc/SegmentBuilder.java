package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.util.DateUtil;

/**
 * VBA CalcFactor_ByYear 내부 cur-loop 1:1 이식
 *
 * 규칙:
 * - intFrom ~ intTo (inclusive)
 * - days 계산/보정 없음
 * - 경계는 Rate / MGR / RateAdjust / yearEnd 만 사용
 */
public class SegmentBuilder {

    public static class DateSegment {
        public final LocalDate intFrom;
        public final LocalDate intTo;
        public final double baseRate;
        public final double mgrRate;

        public DateSegment(LocalDate intFrom, LocalDate intTo,
                double baseRate, double mgrRate) {
            this.intFrom = intFrom;
            this.intTo = intTo;
            this.baseRate = baseRate;
            this.mgrRate = mgrRate;
        }
    }

    public static List<DateSegment> buildYearSegments(
            LocalDate yearStart,
            LocalDate yearEnd,
            LocalDate contractDate,
            List<RateSegment> rateArr,
            List<MinGuaranteedRateSegment> mgrArr,
            List<RateAdjustRule> rateAdjustRules) {

        List<DateSegment> result = new ArrayList<>();

        // VBA: cur = DateAdd("d", 1, yearStart)
        LocalDate cur = yearStart.plusDays(1);

        while (!cur.isAfter(yearEnd)) {

            RateSeg rateSeg = findRateSeg(rateArr, cur);
            if (rateSeg == null)
                break;

            MgrSeg mgrSeg = findMgrSeg(mgrArr, contractDate, cur);

            LocalDate nextElapsedCut = getNextElapsedYearCut(rateAdjustRules, mgrArr, contractDate, cur);

            LocalDate next = min(
                    rateSeg.to.plusDays(1),
                    mgrSeg.to.plusDays(1),
                    nextElapsedCut,
                    yearEnd.plusDays(1));

            LocalDate intFrom = cur;
            LocalDate intTo = next.minusDays(1);
            if (intTo.isAfter(yearEnd))
                intTo = yearEnd;

            if (!intFrom.isAfter(intTo)) {
                result.add(new DateSegment(
                        intFrom, intTo, rateSeg.rate, mgrSeg.rate));
            }

            cur = next;
        }

        return result;
    }

    // ===== helpers =====

    private static class RateSeg {
        LocalDate to;
        double rate;
    }

    private static RateSeg findRateSeg(List<RateSegment> rateArr, LocalDate asOf) {
        for (RateSegment r : rateArr) {
            if (!asOf.isBefore(r.getFromDate())
                    && !asOf.isAfter(r.getToDate())) {
                RateSeg rs = new RateSeg();
                rs.to = r.getToDate();
                rs.rate = r.getRate();
                return rs;
            }
        }
        return null;
    }

    private static class MgrSeg {
        LocalDate to;
        double rate;
    }

    private static MgrSeg findMgrSeg(
            List<MinGuaranteedRateSegment> mgrArr,
            LocalDate contractDate,
            LocalDate asOf) {

        MgrSeg res = new MgrSeg();
        res.rate = 0.0;
        res.to = LocalDate.of(9999, 12, 31);

        if (mgrArr == null)
            return res;

        long y = DateUtil.elapsedYears(contractDate, asOf);

        for (MinGuaranteedRateSegment seg : mgrArr) {
            if (y >= seg.getYearFrom() && y <= seg.getYearTo()) {
                res.rate = seg.getRate();
                res.to = contractDate
                        .plusYears(seg.getYearTo() + 1)
                        .minusDays(1);
                return res;
            }
        }
        return res;
    }

    private static LocalDate getNextElapsedYearCut(
            List<RateAdjustRule> rateAdjustRules,
            List<MinGuaranteedRateSegment> mgrArr,
            LocalDate contractDate,
            LocalDate cur) {

        LocalDate next = LocalDate.of(9999, 12, 31);
        Set<Integer> years = SegmentBuilder.collectElapsedYearCuts(rateAdjustRules, mgrArr);

        for (Integer y : years) {
            LocalDate d = contractDate.plusYears(y).plusDays(1);
            if (d.isAfter(cur) && d.isBefore(next)) {
                next = d;
            }
        }
        return next;
    }

    public static Set<Integer> collectElapsedYearCuts(
            List<RateAdjustRule> rateAdjustRules,
            List<MinGuaranteedRateSegment> mgrArr) {

        java.util.LinkedHashSet<Integer> set = new java.util.LinkedHashSet<>();

        if (rateAdjustRules != null) {
            for (RateAdjustRule r : rateAdjustRules) {
                if (r.hasYearRange()) {
                    set.add(r.getYearFrom());
                }
            }
        }

        if (mgrArr != null) {
            for (MinGuaranteedRateSegment m : mgrArr) {
                set.add(m.getYearFrom());
            }
        }
        return set;
    }

    private static LocalDate min(
            LocalDate a, LocalDate b, LocalDate c, LocalDate d) {

        LocalDate m = a;
        if (b.isBefore(m))
            m = b;
        if (c.isBefore(m))
            m = c;
        if (d.isBefore(m))
            m = d;
        return m;
    }
}
