package com.interestcalc.domain;

import java.time.LocalDate;

public class Step2Detail {

    public final String plyNo;
    public final LocalDate fromDate;
    public final LocalDate toDate;

    public final double beginBalance; // baseBalYear
    public final double annualExpense; // annualCF
    public final double baseAfterAnnual; // baseAfterAnnual

    public final double factor;
    public final double endBalance;

    public Step2Detail(
            String plyNo,
            LocalDate fromDate,
            LocalDate toDate,
            double beginBalance,
            double annualExpense,
            double baseAfterAnnual,
            double factor,
            double endBalance) {

        this.plyNo = plyNo;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.beginBalance = beginBalance;
        this.annualExpense = annualExpense;
        this.baseAfterAnnual = baseAfterAnnual;
        this.factor = factor;
        this.endBalance = endBalance;
    }
}
