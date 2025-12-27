package com.interestcalc.domain;

import java.time.LocalDate;

public class Step1Summary {

    public String plyNo;

    public double balance;

    /** Step1 종료일 (min(calcBaseDate, payEndDate)) */
    public LocalDate step1EndDate;

    /* 연금개시일 */
    public LocalDate annuityDate;

    /** 계약일 */
    public LocalDate contractDate;

    public String rateCode;
    public String productCode;
    public String expenseKey;

    /** 연금납입기간 */
    public long annuityTerm;
    /** 보험종료일 */
    public LocalDate insEndDate;
}