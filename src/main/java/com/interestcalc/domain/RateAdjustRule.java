package com.interestcalc.domain;

public class RateAdjustRule {

    private final String rateCode;

    private final RateAdjustRuleType baseRule;
    private final double baseAdj;

    private final RateAdjustRuleType subRule;
    private final double subAdj;

    private final Integer yearFrom;
    private final Integer yearTo;

    public RateAdjustRule(
            String rateCode,
            RateAdjustRuleType baseRule,
            double baseAdj,
            RateAdjustRuleType subRule,
            double subAdj,
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

    public String getRateCode() {
        return rateCode;
    }

    public RateAdjustRuleType getBaseRule() {
        return baseRule;
    }

    public double getBaseAdj() {
        return baseAdj;
    }

    public RateAdjustRuleType getSubRule() {
        return subRule;
    }

    public double getSubAdj() {
        return subAdj;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public boolean hasSubRule() {
        return subRule != RateAdjustRuleType.NONE;
    }

    /** VBA 기준: YEAR_FROM/YEAR_TO 존재 여부 */
    public boolean hasYearRange() {
        return yearFrom != null && yearTo != null;
    }

    /** 해당 경과연에 적용되는지 */
    public boolean isApplicable(long elapsedYear) {
        if (!hasYearRange()) {
            return true; // VBA와 동일: SubRule 없으면 무조건 적용
        }
        return elapsedYear >= yearFrom && elapsedYear <= yearTo;
    }
}
