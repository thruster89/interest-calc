package com.interestcalc.domain;

import java.time.LocalDate;

public class RateSegment {

    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final double rate;

    public RateSegment(LocalDate fromDate, LocalDate toDate, double rate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.rate = rate;
    }

    public boolean contains(LocalDate date) {
        return (date.isEqual(fromDate) || date.isAfter(fromDate))
                && (date.isEqual(toDate) || date.isBefore(toDate));
    }

    public double getRate() {
        return rate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
