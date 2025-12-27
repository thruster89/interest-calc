package com.interestcalc.domain;

import java.time.LocalDate;

public class Step3Summary {

    public String plyNo;
    public double balance; // Step3 종료 잔액

    public LocalDate calcBaseDate; // Step3 종료 기준일

    public String rateCode;
    public String productCode; // GDCD
    public String expenseKey; // PR_BZCS_DSCNO
    public long annuityTerm; // AN_PY_TRM

    // ===== 편의 생성자 (선택) =====
    public static Step3Summary of(
            Step2Summary s2,
            double balance,
            LocalDate calcBaseDate) {
        Step3Summary s = new Step3Summary();
        s.plyNo = s2.plyNo;
        s.balance = balance;
        s.calcBaseDate = calcBaseDate;
        s.rateCode = s2.rateCode;
        s.productCode = s2.productCode;
        s.expenseKey = s2.expenseKey;
        s.annuityTerm = s2.annuityTerm;
        return s;
    }
}
