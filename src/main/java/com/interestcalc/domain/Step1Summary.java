package com.interestcalc.domain;

import java.time.LocalDate;

public class Step1Summary {

    public String plyNo;

    public double totalBalance;
    public double dedAmtLast;
    public double netBalance;

    public LocalDate step1EndDate;

    public LocalDate annuityDate;
    public LocalDate contractDate;

    public String rateCode;
    public String productCode;
    public String expenseKey;

    public int annuityTerm;
    public LocalDate insEndDate;

    private Step1Summary() {
    }

    public static Step1Summary fromContract(
            Contract c,
            LocalDate step1EndDate) {

        Step1Summary s = new Step1Summary();
        s.plyNo = c.getPlyNo();
        s.contractDate = c.getInsStartDate();
        s.annuityDate = c.getAnnuityDate();
        s.rateCode = c.getRateCode();
        s.productCode = c.getProductCode();
        s.expenseKey = c.getExpenseKey();
        s.annuityTerm = c.getAnnuityTerm();
        s.insEndDate = c.getInsEndDate();
        s.step1EndDate = step1EndDate;
        return s;
    }

    public void applyResult(double totalBalance, double dedAmtLast) {
        this.totalBalance = totalBalance;
        this.dedAmtLast = dedAmtLast;
        this.netBalance = totalBalance - dedAmtLast;
    }

    public void merge(Step1Summary other) {
        if (other.step1EndDate.isAfter(this.step1EndDate)) {
            applyResult(other.totalBalance, other.dedAmtLast);
            this.step1EndDate = other.step1EndDate;
        }
    }
}
