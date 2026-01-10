package com.interestcalc.context;

import java.time.LocalDate;

public class CalcRunContext {

    public final String runMode; // ALL | ONE
    public final String targetPlyNo; // ONE일 때만
    public final CalcBaseDateType baseDateType; // FIXED_DATE | CONTRACT
    public final LocalDate calcBaseDate; // FIXED_DATE 용
    public final int contractYear; // CONTRACT 용
    public final boolean debugMode;

    public CalcRunContext(
            String runMode,
            String targetPlyNo,
            CalcBaseDateType baseDateType,
            LocalDate calcBaseDate,
            int contractYear,
            boolean debugMode) {

        this.runMode = runMode;
        this.targetPlyNo = targetPlyNo;
        this.baseDateType = baseDateType;
        this.calcBaseDate = calcBaseDate;
        this.contractYear = contractYear;
        this.debugMode = debugMode;
    }

    public boolean isOneMode() {
        return "ONE".equalsIgnoreCase(runMode);
    }
}
