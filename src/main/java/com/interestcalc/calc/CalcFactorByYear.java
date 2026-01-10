package com.interestcalc.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.util.DateUtil;

public class CalcFactorByYear {

    /**
     * VBA CalcFactor_ByYear 이식
     * - startDate 포함
     * - endDate 제외
     */
    public static double calc(
            CalcContext ctx,
            LocalDate startDate,
            LocalDate endDate) {

        double totalFactor = 1.0;
        LocalDate yearStart = startDate;
        ctx.yearIdx = 1;

        while (yearStart.isBefore(endDate)) {

            ctx.isFirstSegInYear = true;

            // ===== [1] YEAR_START =====
            if (ctx.debugMode) {
                ctx.debugRows.add(
                        CalcDebugRow.yearStart(
                                ctx.applyTag,
                                ctx.plyNo,
                                ctx.depositSeq,
                                ctx.yearIdx,
                                yearStart,
                                ctx.principal,
                                totalFactor));
            }

            LocalDate yearEnd = yearStart.plusYears(1);
            long yearDays = DateUtil.hasFeb29(yearStart, yearEnd) ? 366 : 365;

            if (yearEnd.isAfter(endDate)) {
                yearEnd = endDate;
            }

            double yearSum = 0.0;

            // ===============================
            // Rate Segments
            // ===============================
            for (RateSegment rateSeg : ctx.rateArr) {

                LocalDate segFrom = max(yearStart, rateSeg.getFromDate());
                LocalDate segTo = min(yearEnd, rateSeg.getToDate());

                if (!segFrom.isBefore(segTo)) {
                    continue;
                }

                // ===============================
                // Elapsed-Year CUT 수집
                // ===============================
                Set<Integer> yearCuts = SegmentBuilder.collectElapsedYearCuts(
                        ctx.rateAdjustRules,
                        ctx.mgrArr);

                List<LocalDate> elapsedCuts = SegmentBuilder.buildElapsedYearCuts(
                        ctx.contractDate,
                        yearCuts,
                        segFrom,
                        segTo);

                List<LocalDate> allCuts = SegmentBuilder.mergeAndSortCuts(
                        segFrom,
                        segTo,
                        elapsedCuts);

                List<SegmentBuilder.DateSegment> subSegs = SegmentBuilder.splitPeriodByCuts(allCuts);

                // ===============================
                // Sub-Segment 누적
                // ===============================
                for (SegmentBuilder.DateSegment sub : subSegs) {

                    long days = ChronoUnit.DAYS.between(sub.fromDate, sub.toDate)
                            + (ctx.isFirstSegInYear ? 0 : 1);

                    double acc = accumulateSegment(
                            ctx,
                            sub.fromDate,
                            sub.toDate,
                            rateSeg.getRate());
                    yearSum += acc;

                    // ===== DETAIL DEBUG =====
                    if (ctx.debugMode) {

                        int elapsedY = (int) DateUtil.elapsedYears(
                                ctx.contractDate,
                                sub.fromDate);

                        double mgrRate = resolveMgrRate(
                                ctx.mgrArr,
                                ctx.contractDate,
                                sub.fromDate);

                        double adjRate = (rateSeg.getRate() + ctx.rateAdd) * ctx.rateMul;

                        double appliedRate = Math.max(adjRate, mgrRate);

                        ctx.debugRows.add(
                                CalcDebugRow.detail(
                                        ctx.applyTag,
                                        ctx.plyNo,
                                        ctx.depositSeq,
                                        ctx.yearIdx,
                                        elapsedY,
                                        sub.fromDate,
                                        sub.toDate,
                                        days,
                                        rateSeg.getRate(),
                                        ctx.rateAdd,
                                        ctx.rateMul,
                                        adjRate,
                                        mgrRate,
                                        appliedRate,
                                        acc,
                                        yearSum));
                    }
                }
            }

            totalFactor *= (1.0 + yearSum / yearDays);

            // ===== [3] YEAR_END =====
            if (ctx.debugMode) {
                ctx.debugRows.add(
                        CalcDebugRow.yearEnd(
                                ctx.applyTag,
                                ctx.plyNo,
                                ctx.depositSeq,
                                ctx.yearIdx,
                                yearEnd,
                                yearDays,
                                yearSum,
                                ctx.principal,
                                totalFactor));
            }

            yearStart = yearEnd;
            ctx.yearIdx++;
        }

        return totalFactor;
    }

    // =====================================================
    // Sub-Segment 누적 (VBA AccumulateSegment 대응)
    // =====================================================
    public static double accumulateSegment(
            CalcContext ctx,
            LocalDate segFrom,
            LocalDate segTo,
            double baseRate) {

        long days = ChronoUnit.DAYS.between(segFrom, segTo); // [from, to)

        if (!ctx.isFirstSegInYear) {
            days += 1;
        }

        // RateAdjust (as-of-date 기준)
        RateAdjustApplier.applyForDate(ctx, segFrom);

        // MGR
        double mgrRate = resolveMgrRate(
                ctx.mgrArr,
                ctx.contractDate,
                segFrom);

        double appliedRate = Math.max(
                (baseRate + ctx.rateAdd) * ctx.rateMul,
                mgrRate);

        double acc = appliedRate * days;

        ctx.isFirstSegInYear = false;

        return acc;
    }

    // =====================================================
    // MGR 조회 (VBA GetMGR 대응)
    // =====================================================
    private static double resolveMgrRate(
            List<MinGuaranteedRateSegment> mgrArr,
            LocalDate contractDate,
            LocalDate asOfDate) {

        if (mgrArr == null || mgrArr.isEmpty()) {
            return 0.0;
        }

        long elapsedY = DateUtil.elapsedYears(contractDate, asOfDate);

        for (MinGuaranteedRateSegment seg : mgrArr) {
            if (elapsedY >= seg.getYearFrom()
                    && elapsedY <= seg.getYearTo()) {
                return seg.getRate();
            }
        }

        return 0.0;
    }

    // =====================================================
    // util
    // =====================================================
    private static LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
