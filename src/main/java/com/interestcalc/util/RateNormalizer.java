package com.interestcalc.util;

public final class RateNormalizer {

    private RateNormalizer() {
    }

    /**
     * 5.2 -> 0.052
     * 0.052 -> 0.052
     */
    public static double normalize(double rate) {
        if (Math.abs(rate) >= 1.0) {
            return rate / 100.0;
        }
        return rate;
    }
}