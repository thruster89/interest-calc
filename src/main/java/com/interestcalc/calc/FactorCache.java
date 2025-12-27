package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.interestcalc.context.CalcContext;

public class FactorCache {

    private static final Map<FactorKey, Double> CACHE = new HashMap<>();

    public static double getFactor(
            CalcContext ctx,
            LocalDate start,
            LocalDate end) {

        FactorKey key = new FactorKey(ctx.plyNo, start, end);

        Double cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        double factor = CalcFactorByYear.calc(ctx, start, end);
        CACHE.put(key, factor);
        return factor;
    }

    public static void clear() {
        CACHE.clear();
    }
}
