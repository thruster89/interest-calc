package com.interestcalc.domain;

public class RateAdjustRule {

    public String rateCode;

    public int baseRule; // 2: 차감, 3: 곱하기
    public double baseAdj;

    public Integer subRule; // null 이면 없음
    public Double subAdj;

    public Integer yearFrom;
    public Integer yearTo;

    public RateAdjustRule(
            String rateCode,
            int baseRule,
            double baseAdj,
            Integer subRule,
            Double subAdj,
            Integer yearFrom,
            Integer yearTo) {
        this.rateCode = rateCode;
        this.baseRule = baseRule;
        this.baseAdj = baseAdj;
        this.subRule = subRule;
        this.subAdj = subAdj;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    public boolean hasSubRule() {
        return subRule != null
                && subAdj != null
                && yearFrom != null
                && yearTo != null;
    }

    public boolean isSubRuleApplicable(int elapsedYear) {
        if (!hasSubRule())
            return false;
        return elapsedYear >= yearFrom && elapsedYear <= yearTo;
    }

    public int getEffectiveRule(int elapsedYear) {
        if (isSubRuleApplicable(elapsedYear)) {
            return subRule;
        }
        return baseRule;
    }

    public double getEffectiveAdj(int elapsedYear) {
        if (isSubRuleApplicable(elapsedYear)) {
            return subAdj;
        }
        return baseAdj;
    }
}
