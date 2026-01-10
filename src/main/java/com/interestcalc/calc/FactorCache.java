package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.interestcalc.context.CalcContext;

public class FactorCache {

    private static final Map<FactorKey, Double> CACHE = new HashMap<>();

    public static double getFactor(
            CalcContext ctx,
            LocalDate startDate,
            LocalDate endDate) {

        // 🔴 DEBUG MODE → 캐시 무시
        if (ctx.debugMode) {
            return CalcFactorByYear.calc(ctx, startDate, endDate);
        }

        FactorKey key = new FactorKey(
                ctx.plyNo,
                ctx.depositSeq,
                startDate,
                endDate);

        Double cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        double factor = CalcFactorByYear.calc(ctx, startDate, endDate);
        CACHE.put(key, factor);
        return factor;
    }

    public static void clear() {
        CACHE.clear();
    }
}
