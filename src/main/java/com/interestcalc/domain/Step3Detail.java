package com.interestcalc.domain;

import java.time.LocalDate;

public class Step3Detail {

    public String plyNo;

    // 기간
    public LocalDate fromDate; // 연초 (curDate)
    public LocalDate toDate; // 연말 (curDate + 1y)

    // 금액 흐름
    public double beginBalance; // 연초 잔액
    public double annuityAmount; // 연금연액
    public double annualExpense; // 연 사업비
    public double interest; // 연 이자
    public double endBalance; // 연말 잔액

    // 계산 보조
    public double factor; // 연 이자 factor
}
