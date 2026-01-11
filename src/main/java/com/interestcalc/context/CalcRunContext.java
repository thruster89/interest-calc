package com.interestcalc.context;

import java.time.LocalDate;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CalcRunContext {

    public final String runMode; // ALL | ONE
    public final String targetPlyNo; // ONE일 때만
    public final CalcBaseDateType baseDateType; // FIXED_DATE | CONTRACT
    public final LocalDate calcBaseDate; // FIXED_DATE 용
    public final int contractYear; // CONTRACT 용
    public final boolean debugMode;

    public CalcRunContext(RunParamHolder params) {

        this.runMode = params.getRunMode();
        this.targetPlyNo = params.getTargetPlyNo();
        this.baseDateType = params.getBaseDateType();
        // this.calcBaseDate = params.getCalcBaseDate();
        this.contractYear = params.getContractYear();
        this.debugMode = params.isDebugMode();
        this.calcBaseDate = resolveBaseDate(params);
    }

    private LocalDate resolveBaseDate(RunParamHolder params) {

        if (params.getBaseDateType() == CalcBaseDateType.FIXED) {
            return params.getCalcBaseDate();
        }

        // CONTRACT
        // 연도 + 계약 MM-DD는 나중에 계약별로 보정
        // 여기서는 "연도 정보만 의미 있음"
        return null;
    }

    public boolean isOneMode() {
        return "ONE".equalsIgnoreCase(runMode);
    }
}
