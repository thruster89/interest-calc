package com.interestcalc.batch.dto;

import java.time.LocalDate;

import com.interestcalc.domain.Contract;

public class DepositJoinRow {

    private String plyNo;
    private LocalDate insStartDate;
    private LocalDate insEndDate;
    private LocalDate annuityDate;

    private String productCode;
    private String rateCode;
    private String expenseKey;

    private int payYears;
    private int deferYears;
    private int annuityTerm;

    private double expectedInterest;
    private double deductible;

    private String pymCyccd;

    private long depositSeq;
    private LocalDate depositDate;
    private double principal;

    // ===== getters =====

    public String getPlyNo() {
        return plyNo;
    }

    public LocalDate getInsStartDate() {
        return insStartDate;
    }

    public LocalDate getInsEndDate() {
        return insEndDate;
    }

    public LocalDate getAnnuityDate() {
        return annuityDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getRateCode() {
        return rateCode;
    }

    public String getExpenseKey() {
        return expenseKey;
    }

    public int getPayYears() {
        return payYears;
    }

    public int getAnnuityTerm() {
        return annuityTerm;
    }

    public double getDeductible() {
        return deductible;
    }

    public String getPymCyccd() {
        return pymCyccd;
    }

    public long getDepositSeq() {
        return depositSeq;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public double getPrincipal() {
        return principal;
    }

    // ===== helper =====
    public Contract toContract() {
        return new Contract(
                plyNo,
                insStartDate,
                productCode,
                rateCode,
                insEndDate,
                payYears,
                deferYears,
                annuityDate,
                expectedInterest,
                deductible,
                0.0,
                expenseKey,
                pymCyccd,
                null,
                null,
                0,
                annuityTerm);
    }
}
