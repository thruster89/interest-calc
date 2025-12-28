package com.interestcalc.rate;

import java.util.List;
import java.util.Map;

import com.interestcalc.domain.RateAdjustRule;

/**
 * RATE_ADJUST Resolver
 * - BaseRule 기본
 * - SubRule (기간조건) 만족 시 override
 */
public class RateAdjustResolver {

    private final Map<String, List<RateAdjustRule>> ruleMap;

    public RateAdjustResolver(Map<String, List<RateAdjustRule>> ruleMap) {
        this.ruleMap = ruleMap;
    }

    public RateAdjustDecision resolve(String rateCode, int elapsedYear) {

        List<RateAdjustRule> rules = ruleMap.get(rateCode);
        if (rules == null || rules.isEmpty()) {
            return null; // 보정 없음
        }

        // 1) SubRule 우선 탐색
        for (RateAdjustRule r : rules) {
            if (r.hasSubRule()
                    && elapsedYear >= r.yearFrom
                    && elapsedYear <= r.yearTo) {

                return new RateAdjustDecision(
                        r.subRule,
                        r.subAdj);
            }
        }

        // 2) SubRule 없으면 BaseRule 사용 (첫 행 기준)
        RateAdjustRule base = rules.get(0);
        return new RateAdjustDecision(
                base.baseRule,
                base.baseAdj);
    }
}
