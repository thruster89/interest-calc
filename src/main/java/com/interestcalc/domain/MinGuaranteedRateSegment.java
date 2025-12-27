package com.interestcalc.domain;

public class MinGuaranteedRateSegment {

    private final int yearFrom;
    private final int yearTo;
    private final double rate;

    public MinGuaranteedRateSegment(int yearFrom, int yearTo, double rate) {
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.rate = rate;
    }

    public boolean matches(int elapsedYear) {
        return elapsedYear >= yearFrom && elapsedYear <= yearTo;
    }

    public double getRate() {
        return rate;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }
}
