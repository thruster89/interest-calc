package com.interestcalc.domain;

import java.time.LocalDate;

/**
 * VBA Step3_Detail 대응
 */
public record Step3Detail(

        String plyNo,

        LocalDate fromDate,
        LocalDate toDate,

        double beginBalance,
        double interest,

        double annAmount,
        double annualExpense,

        double endBalance,

        double factor,

        int yearIdx,
        int remainYears,

        double discountFactor) {
}
