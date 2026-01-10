package com.interestcalc.domain;

import java.time.LocalDate;

public class Step1Summary {

    public String plyNo;

    public double netBalance; // totalBalance - dedAmtLast
    public LocalDate step1EndDate;

    public LocalDate annuityDate; // VBA info(6)
    public LocalDate contractDate;

    public String rateCode;
    public String productCode;

    public String expenseKey;
    public int annuityTerm;

    public LocalDate insEndDate;

    public double totalBalance;
    public double dedAmtLast;

    private Step1Summary() {
    }

    // ===== VBA Summary 1줄 생성자 =====
    public static Step1Summary fromContract(
            Contract c,
            LocalDate step1EndDate) {

        Step1Summary s = new Step1Summary();

        s.plyNo = c.getPlyNo();
        s.step1EndDate = step1EndDate;

        s.annuityDate = c.getAnnuityDate();
        s.contractDate = c.getInsStartDate();

        s.rateCode = c.getRateCode();
        s.productCode = c.getProductCode();

        s.expenseKey = c.getExpenseKey();
        s.annuityTerm = c.getAnnuityTerm();

        s.insEndDate = c.getInsEndDate();

        return s;
    }

    // ===== 계산 결과 세팅 =====
    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setDeductAmount(double dedAmtLast) {
        this.dedAmtLast = dedAmtLast;
    }

    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }

    // ===== CSV Loader 전용 생성 =====
    public static Step1Summary fromCsv(
            String plyNo,
            double netBalance,
            LocalDate step1EndDate,
            LocalDate annuityDate,
            LocalDate contractDate,
            String rateCode,
            String productCode,
            String expenseKey,
            int annuityTerm,
            LocalDate insEndDate,
            double totalBalance,
            double dedAmtLast) {

        Step1Summary s = new Step1Summary();

        s.plyNo = plyNo;
        s.netBalance = netBalance;
        s.step1EndDate = step1EndDate;
        s.annuityDate = annuityDate;
        s.contractDate = contractDate;
        s.rateCode = rateCode;
        s.productCode = productCode;
        s.expenseKey = expenseKey;
        s.annuityTerm = annuityTerm;
        s.insEndDate = insEndDate;
        s.totalBalance = totalBalance;
        s.dedAmtLast = dedAmtLast;

        return s;
    }
}
