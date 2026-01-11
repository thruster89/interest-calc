package com.interestcalc.calc;

public final class DeductionCalculator {

    private DeductionCalculator() {
        // static utility
    }

    public static double calc(String pymCyccd, long payterm, long depositSeq, double deductible) {

        // String pymCyccd = c.getPymCyccd();

        // 일시납
        if ("00".equals(pymCyccd)) {
            return 0.0;
        }

        long amortYears = Math.min(payterm, 7);
        long amortMonths = amortYears * 12;

        long adjSeq = depositSeq * Long.parseLong(pymCyccd);

        double rate = Math.max(
                (amortMonths - adjSeq) / (double) amortMonths,
                0.0);

        return deductible * rate;
    }
}
