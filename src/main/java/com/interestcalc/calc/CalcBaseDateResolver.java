package com.interestcalc.calc;

import java.time.LocalDate;
import java.time.Month;

import com.interestcalc.context.CalcRunContext;

public class CalcBaseDateResolver {

    /**
     * VBA ResolveCalcBaseDate 1:1 이식
     *
     * @param runCtx       실행 파라미터 (CALC_MODE, critYear, fixedDate)
     * @param contractDate 계약일자
     * @return 계산 기준일
     */
    public static LocalDate resolve(
            CalcRunContext runCtx,
            LocalDate contractDate) {

        switch (runCtx.baseDateType) {

            case FIXED -> {
                return runCtx.calcBaseDate;
            }

            case CONTRACT -> {
                int critYear = runCtx.contractYear;

                int mm = contractDate.getMonthValue();
                int dd = contractDate.getDayOfMonth();

                // VBA 윤년 보정 로직 그대로
                if (mm == 2 && dd == 29 && !isLeapYear(critYear)) {
                    return LocalDate.of(critYear, Month.FEBRUARY, 28);
                }

                return LocalDate.of(critYear, mm, dd);
            }

            default -> throw new IllegalArgumentException(
                    "Invalid CalcBaseDateType: " + runCtx.baseDateType);
        }
    }

    private static boolean isLeapYear(int year) {
        return java.time.Year.isLeap(year);
    }
}
