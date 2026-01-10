package com.interestcalc.domain;

import java.time.LocalDate;

/**
 * Contract master (CSV 1:1 보존용)
 */
public class Contract {

    // ===== KEY =====
    private final String plyNo; // PLYNO

    // ===== Dates =====
    private final LocalDate insStartDate; // INS_ST
    private final LocalDate insEndDate; // INS_CLSTR
    private final LocalDate annuityDate; // AN_PY_STDT

    // ===== Codes =====
    private final String productCode; // GDCD
    private final String rateCode; // RATE_CODE
    private final String expenseKey; // PR_BZCS_DSCNO

    // ===== Terms =====
    private final int payYears; // RL_PYM_TRM
    private final int deferYears; // DFR_TRM
    private final int annuityTerm; // AN_PY_TRM

    // ===== Amounts / Rates =====
    private final double expectedInterest; // EXPCT_INRT
    private final double deductible; // PR_NWCRT_TAMT
    private final double carryForwardDeductible; // CRFW_PR_NWCRT_TAMT

    // ===== Flags / Codes =====
    private final String pymCyccd; // PYM_CYCCD
    private final String annInsTrmFlgcd; // AN_INS_TRM_FLGCD
    private final String annPytcd; // AN_PYTCD
    private final int annPyGirt; // AN_PY_GIRT

    public Contract(
            String plyNo,
            LocalDate insStartDate,
            String productCode,
            String rateCode,
            LocalDate insEndDate,
            int payYears,
            int deferYears,
            LocalDate annuityDate,
            double expectedInterest,
            double deductible,
            double carryForwardDeductible,
            String expenseKey,
            String pymCyccd,
            String annInsTrmFlgcd,
            String annPytcd,
            int annPyGirt,
            int annuityTerm) {

        this.plyNo = plyNo;
        this.insStartDate = insStartDate;
        this.productCode = productCode;
        this.rateCode = rateCode;
        this.insEndDate = insEndDate;
        this.payYears = payYears;
        this.deferYears = deferYears;
        this.annuityDate = annuityDate;
        this.expectedInterest = expectedInterest;
        this.deductible = deductible;
        this.carryForwardDeductible = carryForwardDeductible;
        this.expenseKey = expenseKey;
        this.pymCyccd = pymCyccd;
        this.annInsTrmFlgcd = annInsTrmFlgcd;
        this.annPytcd = annPytcd;
        this.annPyGirt = annPyGirt;
        this.annuityTerm = annuityTerm;
    }

    // ===== getters (전부 제공) =====
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

    public int getDeferYears() {
        return deferYears;
    }

    public int getAnnuityTerm() {
        return annuityTerm;
    }

    public double getExpectedInterest() {
        return expectedInterest;
    }

    public double getDeductible() {
        return deductible;
    }

    public double getCarryForwardDeductible() {
        return carryForwardDeductible;
    }

    public String getPymCyccd() {
        return pymCyccd;
    }

    public String getAnnInsTrmFlgcd() {
        return annInsTrmFlgcd;
    }

    public String getAnnPytcd() {
        return annPytcd;
    }

    public int getAnnPyGirt() {
        return annPyGirt;
    }
}
