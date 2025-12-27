package com.interestcalc.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // VBA HasFeb29
    public static boolean hasFeb29(LocalDate from, LocalDate to) {
        LocalDate d = from;
        while (d.isBefore(to)) {
            if (d.getMonthValue() == 2 && d.getDayOfMonth() == 29) {
                return true;
            }
            d = d.plusDays(1);
        }
        return false;
    }

    // VBA ElapsedYears
    public static long elapsedYears(LocalDate contractDate, LocalDate asOf) {
        long y = ChronoUnit.YEARS.between(contractDate, asOf);
        // 도달일 포함: n년 도달 시 (n+1)년차
        if (!asOf.isBefore(contractDate.plusYears(y))) {
            y++;
        }
        return y;
    }

    /**
     * VBA NextMonthlyDate 대체
     */
    public static LocalDate nextMonthlyDate(LocalDate base, int chargeDay) {

        LocalDate nextMonth = base.plusMonths(1);
        int year = nextMonth.getYear();
        int month = nextMonth.getMonthValue();

        int day = clampDayToEOM(year, month, chargeDay);
        return LocalDate.of(year, month, day);
    }

    /**
     * 말일 보정
     */
    public static int clampDayToEOM(int year, int month, int day) {
        int eom = YearMonth.of(year, month).lengthOfMonth();
        return Math.min(day, eom);
    }
}
