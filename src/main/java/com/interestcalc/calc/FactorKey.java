package com.interestcalc.calc;

import java.time.LocalDate;

public record FactorKey(
                String plyNo,
                int depositSeq,
                LocalDate startDate,
                LocalDate endDate) {
}
