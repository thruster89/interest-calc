package com.interestcalc.domain;

import java.time.LocalDate;

public class Contract {

    private final String plyNo;

    private final LocalDate insStartDate; // INS_ST
    private final LocalDate insEndDate; // INS_CLSTR

    private final String productCode; // GDCD
    private final String rateCode; // RATE_CODE

    private final int payYears; // RL_PYM_TRM ★
    private final int deferYears; // DFR_TRM

    private final LocalDate annuityDate; // AN_PY_STDT
    private final int annuityTerm; // AN_PY_TRM

    private final String expenseKey; // PR_BZCS_DSCNO

    public Contract(
            String plyNo,
            LocalDate insStartDate,
            LocalDate insEndDate,
            String productCode,
            String rateCode,
            int payYears,
            int deferYears,
            LocalDate annuityDate,
            int annuityTerm,
            String expenseKey) {
        this.plyNo = plyNo;
        this.insStartDate = insStartDate;
        this.insEndDate = insEndDate;
        this.productCode = productCode;
        this.rateCode = rateCode;
        this.payYears = payYears;
        this.deferYears = deferYears;
        this.annuityDate = annuityDate;
        this.annuityTerm = annuityTerm;
        this.expenseKey = expenseKey;
    }

    public String getPlyNo() {
        return plyNo;
    }

    public LocalDate getInsStartDate() {
        return insStartDate;
    }

    public LocalDate getInsEndDate() {
        return insEndDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getRateCode() {
        return rateCode;
    }

    /** ★ 여기 */
    public int getPayYears() {
        return payYears;
    }

    public int getDeferYears() {
        return deferYears;
    }

    public LocalDate getAnnuityDate() {
        return annuityDate;
    }

    public int getAnnuityTerm() {
        return annuityTerm;
    }

    public String getExpenseKey() {
        return expenseKey;
    }
}
