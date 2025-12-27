package com.interestcalc.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.util.DateUtil;

public class CalcFactorByYear {

    /**
     * VBA CalcFactor_ByYear 이식
     * startDate 포함, endDate 제외
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

            LocalDate yearEnd = yearStart.plusYears(1);
            long yearDays = DateUtil.hasFeb29(yearStart, yearEnd) ? 366 : 365;

            if (yearEnd.isAfter(endDate)) {
                yearEnd = endDate;
            }

            double yearSum = 0.0;

            if (ctx.debugMode) {
                System.out.printf(
                        "[YEAR] plyNo=%s yearIdx=%d from=%s to=%s yearDays=%d%n",
                        ctx.plyNo,
                        ctx.yearIdx,
                        yearStart,
                        yearEnd,
                        yearDays);
            }

            // ===== rate segments =====
            for (RateSegment seg : ctx.rateArr) {

                LocalDate s = max(yearStart, seg.getFromDate());
                LocalDate e = min(yearEnd, seg.getToDate());

                if (!s.isAfter(e)) {
                    yearSum = accumulateSegment(
                            ctx, s, e, seg.getRate(), yearSum);
                }
            }

            totalFactor *= (1.0 + yearSum / yearDays);

            double yearFactor = 1.0 + yearSum / yearDays;

            if (ctx.debugMode) {
                System.out.printf(
                        "[YEAR END] yearIdx=%d yearSum=%.6f yearFactor=%.12f totalFactor=%.12f%n",
                        ctx.yearIdx,
                        yearSum,
                        yearFactor,
                        totalFactor);
            }

            yearStart = yearEnd;
            ctx.yearIdx++;
        }

        return totalFactor;
    }

    /**
     * VBA AccumulateSegment 대응
     */
    private static double accumulateSegment(
            CalcContext ctx,
            LocalDate segFrom,
            LocalDate segTo,
            double baseRate,
            double yearSum) {

        long days = ChronoUnit.DAYS.between(segFrom, segTo); // [from, to)

        // VBA: 첫 seg 아니면 +1
        if (!ctx.isFirstSegInYear) {
            days += 1;
        }

        double mgrRate = 0.0;
        if (ctx.mgrArr != null) {
            mgrRate = resolveMgrRate(
                    ctx.mgrArr,
                    ctx.contractDate,
                    segFrom);
        }

        double appliedRate = Math.max(baseRate + ctx.rateAdj, mgrRate) / 100.0;

        double acc = appliedRate * days;
        yearSum += acc;

        ctx.isFirstSegInYear = false;

        if (ctx.debugMode) {
            System.out.printf(
                    "  [SEG] %s ~ %s days=%d baseRate=%.6f mgrRate=%.6f applied=%.6f acc=%.6f sum=%.6f%n",
                    segFrom,
                    segTo,
                    days,
                    baseRate,
                    mgrRate,
                    appliedRate,
                    acc,
                    yearSum);
        }
        return yearSum;
    }

    /**
     * VBA GetMGR 대응
     */
    private static double resolveMgrRate(
            List<MinGuaranteedRateSegment> mgrArr,
            LocalDate contractDate,
            LocalDate asOfDate) {

        long elapsedYears = DateUtil.elapsedYears(contractDate, asOfDate);

        for (MinGuaranteedRateSegment seg : mgrArr) {
            if (elapsedYears >= seg.getYearFrom()
                    && elapsedYears <= seg.getYearTo()) {
                return seg.getRate();
            }
        }
        return 0.0;
    }

    private static LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
