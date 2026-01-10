package com.interestcalc.domain;

import java.time.LocalDate;

public class Step2Summary {

    public String plyNo;
    public double balance;

    public LocalDate step2EndDate;
    public LocalDate annuityDate;

    public String rateCode;
    public String productCode;
    public String expenseKey;

    public int annuityTerm;

    public LocalDate insEndDate;
    public LocalDate contractDate;

    public double baseEndBalance;
    public double totalMonthlyExpenseAcc;

    public Step2Summary(
            String plyNo,
            double balance,
            LocalDate step2EndDate,
            LocalDate annuityDate,
            String rateCode,
            String productCode,
            String expenseKey,
            int annuityTerm,
            LocalDate insEndDate,
            LocalDate contractDate,
            double baseEndBalance,
            double totalMonthlyExpenseAcc) {

        this.plyNo = plyNo;
        this.balance = balance;
        this.step2EndDate = step2EndDate;
        this.annuityDate = annuityDate;
        this.rateCode = rateCode;
        this.productCode = productCode;
        this.expenseKey = expenseKey;
        this.annuityTerm = annuityTerm;
        this.insEndDate = insEndDate;
        this.contractDate = contractDate;
        this.baseEndBalance = baseEndBalance;
        this.totalMonthlyExpenseAcc = totalMonthlyExpenseAcc;
    }
}
