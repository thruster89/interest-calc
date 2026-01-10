package com.interestcalc.domain;

import java.time.LocalDate;

/**
 * VBA Step3_Summary 대응
 */
public record Step3Summary(

        String plyNo,

        double balance,

        LocalDate calcBaseDate,

        String rateCode,
        String productCode,
        String expenseKey,

        int annuityTerm) {
}
