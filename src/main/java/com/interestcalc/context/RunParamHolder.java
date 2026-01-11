package com.interestcalc.context;

import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "calc")
public class RunParamHolder {

    private String runMode;
    private String targetPlyNo;
    private CalcBaseDateType baseDateType;
    private LocalDate calcBaseDate;
    private int contractYear;
    private boolean debugMode;

    // ===== getters / setters =====

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getTargetPlyNo() {
        return targetPlyNo;
    }

    public void setTargetPlyNo(String targetPlyNo) {
        this.targetPlyNo = targetPlyNo;
    }

    public CalcBaseDateType getBaseDateType() {
        return baseDateType;
    }

    public void setBaseDateType(CalcBaseDateType baseDateType) {
        this.baseDateType = baseDateType;
    }

    public LocalDate getCalcBaseDate() {
        return calcBaseDate;
    }

    public void setCalcBaseDate(LocalDate calcBaseDate) {
        this.calcBaseDate = calcBaseDate;
    }

    public int getContractYear() {
        return contractYear;
    }

    public void setContractYear(int contractYear) {
        this.contractYear = contractYear;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
