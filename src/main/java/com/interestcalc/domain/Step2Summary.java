package com.interestcalc.domain;

import java.time.LocalDate;

public class Step2Summary {

    public String plyNo;
    public double balance;

    public LocalDate calcBaseDate; // ★ Step2 종료일
    public LocalDate annuityDate;
    public LocalDate insStartDate;
    public LocalDate insClusterDate;

    public String rateCode;
    public String productCode; // GDCD
    public String expenseKey; // PR_BZCS_DSCNO
    public long annuityTerm; // AN_PY_TRM
}
