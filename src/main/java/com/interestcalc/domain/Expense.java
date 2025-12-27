package com.interestcalc.domain;

public class Expense {

    public String expenseKey;
    public double monthlyAmount;
    public double yearlyRate;
    public double ayamtRate;

    public Expense(String expenseKey, double monthlyAmount, double yearlyRate, double ayamtRate) {
        this.expenseKey = expenseKey;
        this.monthlyAmount = monthlyAmount;
        this.yearlyRate = yearlyRate;
        this.ayamtRate = ayamtRate;
    }
}
