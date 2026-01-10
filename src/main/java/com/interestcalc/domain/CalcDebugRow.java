package com.interestcalc.domain;

import java.time.LocalDate;

public record CalcDebugRow(
        String tag,
        String applyTag,
        String plyNo,
        long depositSeq,
        int yearIdx,
        Integer elapsedYears,

        LocalDate fromDate,
        LocalDate toDate,

        Integer days,

        Double baseRate,
        Double rateAdd,
        Double rateMul,
        Double adjRate,
        Double mgrRate,
        Double appliedRate,

        Double acc,
        Double yearSum,

        Double principal,
        Double factor,
        Double balance) {

    // ======================
    // YEAR_START
    // ======================
    public static CalcDebugRow yearStart(
            String applyTag,
            String plyNo,
            long depositSeq,
            int yearIdx,
            LocalDate yearStart,
            double principal,
            double factor) {
        return new CalcDebugRow(
                "YEAR_START",
                applyTag,
                plyNo,
                depositSeq,
                yearIdx,
                null,
                yearStart,
                null,
                null,
                null, null, null, null, null, null,
                null,
                null,
                principal,
                factor,
                principal * factor);
    }

    // ======================
    // DETAIL
    // ======================
    public static CalcDebugRow detail(
            String applyTag,
            String plyNo,
            long depositSeq,
            int yearIdx,
            int elapsedYears,
            LocalDate fromDate,
            LocalDate toDate,
            long days,
            double baseRate,
            double rateAdd,
            double rateMul,
            double adjRate,
            double mgrRate,
            double appliedRate,
            double acc,
            double yearSum) {
        return new CalcDebugRow(
                "DETAIL",
                applyTag,
                plyNo,
                depositSeq,
                yearIdx,
                elapsedYears,
                fromDate,
                toDate,
                (int) days,
                baseRate,
                rateAdd,
                rateMul,
                adjRate,
                mgrRate,
                appliedRate,
                acc,
                yearSum,
                null,
                null,
                null);
    }

    // ======================
    // YEAR_END
    // ======================
    public static CalcDebugRow yearEnd(
            String applyTag,
            String plyNo,
            long depositSeq,
            int yearIdx,
            LocalDate yearEnd,
            long yearDays,
            double yearSum,
            double principal,
            double factor) {
        return new CalcDebugRow(
                "YEAR_END",
                applyTag,
                plyNo,
                depositSeq,
                yearIdx,
                null,
                null,
                yearEnd,
                (int) yearDays,
                null, null, null, null, null, null,
                null,
                yearSum,
                principal,
                factor,
                principal * factor);
    }
}
