package com.interestcalc.domain;

import java.time.LocalDate;

public class Deposit {

    private String plyNo;
    private int depositSeq;
    private LocalDate depositDate;
    private double principal;

    public Deposit() {
    }

    public String getPlyNo() {
        return plyNo;
    }

    public void setPlyNo(String plyNo) {
        this.plyNo = plyNo;
    }

    public int getDepositSeq() {
        return depositSeq;
    }

    public void setDepositSeq(int depositSeq) {
        this.depositSeq = depositSeq;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "plyNo='" + plyNo + '\'' +
                ", depositSeq=" + depositSeq +
                ", depositDate=" + depositDate +
                ", principal=" + principal +
                '}';
    }
}