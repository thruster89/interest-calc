package com.interestcalc.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.util.DateUtil;

/**
 * CalcFactorByYear
 *
 * VBA CalcFactor_ByYear 1:1 이식
 */
public class CalcFactorByYear {

        public static double calc(
                        CalcContext ctx,
                        LocalDate startDate,
                        LocalDate endDate) {

                double totalFactor = 1.0;
                LocalDate yearStart = startDate;
                ctx.rateAdd = 0.0;
                ctx.rateMul = 1.0;
                ctx.isFirstSegInYear = true;
                ctx.yearIdx = 1;
                while (yearStart.isBefore(endDate)) {

                        // ===== YEAR RANGE =====
                        LocalDate yearEnd = yearStart.plusYears(1);
                        long yearDays = DateUtil.hasFeb29(
                                        yearStart,
                                        yearStart.plusYears(1)) ? 366 : 365;

                        if (yearEnd.isAfter(endDate)) {
                                yearEnd = endDate;
                        }

                        double yearSum = 0.0;

                        // ===== YEAR_START DEBUG =====
                        if (ctx.debugMode) {
                                ctx.addDebug(
                                                CalcDebugRow.yearStart(
                                                                ctx.applyTag,
                                                                ctx.plyNo,
                                                                ctx.depositSeq,
                                                                ctx.yearIdx,
                                                                yearStart,
                                                                ctx.principal,
                                                                totalFactor));
                        }

                        // ===== BUILD SEGMENTS =====
                        List<SegmentBuilder.DateSegment> segments = SegmentBuilder.buildYearSegments(
                                        yearStart,
                                        yearEnd,
                                        ctx.contractDate,
                                        ctx.rateArr,
                                        ctx.mgrArr,
                                        ctx.rateAdjustRules);

                        for (SegmentBuilder.DateSegment seg : segments) {

                                long days = ChronoUnit.DAYS.between(
                                                seg.intFrom, seg.intTo) + 1;

                                // RateAdjust (as-of = intFrom)
                                RateAdjustApplier.applyForDate(ctx, seg.intFrom);

                                double calcRate = (seg.baseRate + ctx.rateAdd) * ctx.rateMul;

                                double appliedRate = Math.max(calcRate, seg.mgrRate);

                                double acc = appliedRate * days;
                                yearSum += acc;

                                // ===== SEG DEBUG =====
                                if (ctx.debugMode) {
                                        int elapsedY = (int) DateUtil.elapsedYears(
                                                        ctx.contractDate,
                                                        seg.intFrom);

                                        ctx.addDebug(
                                                        CalcDebugRow.detail(
                                                                        ctx.applyTag,
                                                                        ctx.plyNo,
                                                                        ctx.depositSeq,
                                                                        ctx.yearIdx,
                                                                        elapsedY,
                                                                        seg.intFrom,
                                                                        seg.intTo,
                                                                        days,
                                                                        seg.baseRate,
                                                                        ctx.rateAdd,
                                                                        ctx.rateMul,
                                                                        calcRate,
                                                                        seg.mgrRate,
                                                                        appliedRate,
                                                                        acc,
                                                                        yearSum));
                                }
                        }

                        totalFactor *= (1.0 + yearSum / yearDays);

                        // ===== YEAR_END DEBUG =====
                        if (ctx.debugMode) {
                                ctx.addDebug(
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
}
