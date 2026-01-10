package com.interestcalc.domain;

import java.time.LocalDate;

public class Step1Detail {

    public String plyNo;
    public int depositSeq;
    public LocalDate depositDate;
    public double principal;

    public double factor;
    public double balance;

    public LocalDate step1EndDate;

    public double dedAmt; // VBA: dedAmt (입금별 계산, 마지막 것이 summary에 반영)

    public Step1Detail() {
    }

    public Step1Detail(
            String plyNo,
            int depositSeq,
            LocalDate depositDate,
            double principal,
            double factor,
            double balance,
            LocalDate step1EndDate,
            double dedAmt) {

        this.plyNo = plyNo;
        this.depositSeq = depositSeq;
        this.depositDate = depositDate;
        this.principal = principal;
        this.factor = factor;
        this.balance = balance;
        this.step1EndDate = step1EndDate;
        this.dedAmt = dedAmt;
    }
}
