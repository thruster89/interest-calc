package com.interestcalc.domain;

public enum RateAdjustRuleType {
    NONE(0),
    ADD(1),
    SUBTRACT(2),
    MULTIPLY(3);

    private final int code;

    RateAdjustRuleType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RateAdjustRuleType fromCode(int code) {
        for (RateAdjustRuleType t : values()) {
            if (t.code == code)
                return t;
        }
        return NONE;
    }
}