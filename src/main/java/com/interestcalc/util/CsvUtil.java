package com.interestcalc.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // =====================================================
    // WRITE (Object -> CSV String)
    // =====================================================

    public static String s(String v) {
        return v == null ? "" : v;
    }

    public static String n(Double v) {
        return v == null ? "" : Double.toString(v);
    }

    public static String i(Integer v) {
        return v == null ? "" : Integer.toString(v);
    }

    public static String l(Long v) {
        return v == null ? "" : Long.toString(v);
    }

    public static String d(LocalDate v) {
        return v == null ? "" : v.format(DATE_FMT);
    }

    // =====================================================
    // READ (CSV String -> Object)
    // =====================================================

    public static double parseDouble(String s) {
        if (s == null || s.isBlank()) {
            return 0.0;
        }
        return Double.parseDouble(s.trim());
    }

    public static int parseInt(String s) {
        if (s == null || s.isBlank()) {
            return 0;
        }
        return Integer.parseInt(s.trim());
    }

    public static long parseLong(String s) {
        if (s == null || s.isBlank()) {
            return 0L;
        }
        return Long.parseLong(s.trim());
    }

    public static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return LocalDate.parse(s.trim(), DATE_FMT);
    }

    // ===============================
    // Double (CSV 출력용)
    // ===============================
    public static String fmt(Double v) {
        return v == null ? "" : Double.toString(v);
    }

    public static String fmt(double v) {
        return Double.toString(v);
    }

    // ===============================
    // LocalDate
    // ===============================
    public static String fmt(LocalDate d) {
        return d == null ? "" : d.format(DATE_FMT);
    }
}
