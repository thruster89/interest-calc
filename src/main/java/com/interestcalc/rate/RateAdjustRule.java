package com.interestcalc.rate;

public class RateAdjustRule {

    private final String rateCode;
    private final int yearFrom;
    private final int yearTo;
    private final int rule; // 2: subtract, 3: multiply
    private final double adj1;

    public RateAdjustRule(
            String rateCode,
            int yearFrom,
            int yearTo,
            int rule,
            double adj1) {
        this.rateCode = rateCode;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.rule = rule;
        this.adj1 = adj1;
    }

    public boolean matches(String rateCode, int elapsedYear) {
        return this.rateCode.equals(rateCode)
                && elapsedYear >= yearFrom
                && elapsedYear <= yearTo;
    }

    public double apply(double baseRate) {
        return switch (rule) {
            case 2 -> baseRate - adj1; // -2 → -0.02
            case 3 -> baseRate * adj1; // 1.25 → ×1.25
            default -> baseRate;
        };
    }
}
