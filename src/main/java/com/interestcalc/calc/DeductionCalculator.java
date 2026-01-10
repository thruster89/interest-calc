package com.interestcalc.calc;

import com.interestcalc.domain.Contract;

public final class DeductionCalculator {

    private DeductionCalculator() {
        // static utility
    }

    public static double calc(Contract c, long depositSeq) {

        String pymCyccd = c.getPymCyccd();

        // 일시납
        if ("00".equals(pymCyccd)) {
            return 0.0;
        }

        long amortYears = Math.min(c.getPayYears(), 7);
        long amortMonths = amortYears * 12;

        long adjSeq = depositSeq * Long.parseLong(pymCyccd);

        double rate = Math.max(
                (amortMonths - adjSeq) / (double) amortMonths,
                0.0);

        return c.getDeductible() * rate;
    }
}
