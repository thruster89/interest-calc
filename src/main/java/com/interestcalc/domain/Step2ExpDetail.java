package com.interestcalc.domain;

import java.time.LocalDate;

public class Step2ExpDetail {

    public String plyNo;
    public long seq;

    public LocalDate fromDate;

    public double expenseAmt;
    public double factor;
    public double accAmount;

    public LocalDate step2EndDate;

    public Step2ExpDetail(
            String plyNo,
            long seq,
            LocalDate fromDate,
            double expenseAmt,
            double factor,
            double accAmount,
            LocalDate step2EndDate) {

        this.plyNo = plyNo;
        this.seq = seq;
        this.fromDate = fromDate;
        this.expenseAmt = expenseAmt;
        this.factor = factor;
        this.accAmount = accAmount;
        this.step2EndDate = step2EndDate;
    }
}
