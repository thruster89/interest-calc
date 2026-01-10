package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.List;

import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateAdjustRuleType;
import com.interestcalc.util.DateUtil;

public class RateAdjustApplier {

    /**
     * VBA ApplyRateAdjustForDate 대응
     * - 세그먼트 시작일 기준
     * - Rule 매칭 시에만 Base/Sub 적용
     * - 최초 1건만 적용
     */
    public static void applyForDate(CalcContext ctx, LocalDate asOfDate) {

        // === 세그먼트별 초기화 ===
        ctx.rateAdd = 0.0;
        ctx.rateMul = 1.0;

        List<RateAdjustRule> rules = ctx.rateAdjustRules;
        if (rules == null || rules.isEmpty()) {
            return;
        }

        long elapsedY = DateUtil.elapsedYears(ctx.contractDate, asOfDate);

        for (RateAdjustRule r : rules) {

            // ===== SubRule이 있으면 연차 조건 체크 =====
            if (r.hasYearRange()) {
                if (elapsedY < r.getYearFrom() || elapsedY > r.getYearTo()) {
                    continue; // ❗ VBA의 NEXT_ROW
                }
            }

            // ===== 여기 도달 = 이 Rule이 매칭됨 =====

            // BaseRule
            applyRule(ctx, r.getBaseRule(), r.getBaseAdj());

            // SubRule
            applyRule(ctx, r.getSubRule(), r.getSubAdj());

            // VBA와 동일: 최초 1건만
            break;
        }
    }

    // --------------------------------------------------

    private static void applyRule(
            CalcContext ctx,
            RateAdjustRuleType rule,
            double adj) {

        if (rule == null || rule == RateAdjustRuleType.NONE) {
            return;
        }

        switch (rule) {
            case ADD -> ctx.rateAdd += adj / 100.0;

            case SUBTRACT -> ctx.rateAdd -= adj / 100.0;

            case MULTIPLY -> ctx.rateMul *= adj;

            default -> {
            }
        }
        // no-op
    }
}
