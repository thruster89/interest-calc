package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.List;

import com.interestcalc.context.CalcContext;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateAdjustRuleType;
import com.interestcalc.util.DateUtil;

/**
 * VBA ApplyRateAdjustForDate 이식
 *
 * 규칙:
 * - BaseRule은 항상 적용
 * - SubRule이 있으면 elapsedYear 범위 체크
 * - 최초 매칭 1건만 적용
 * - ctx.rateAdd / ctx.rateMul 누적
 */
public class RateAdjustResolver {

    /**
     * 특정 날짜(asOfDate)에 대한 RateAdjust 적용
     */
    public static void applyForDate(
            CalcContext ctx,
            LocalDate asOfDate) {

        // ===== 기본값 초기화 (VBA와 동일) =====
        ctx.rateAdd = 0.0;
        ctx.rateMul = 1.0;

        List<RateAdjustRule> rules = ctx.rateAdjustRules;
        if (rules == null || rules.isEmpty()) {
            return;
        }

        long elapsedYear = DateUtil.elapsedYears(ctx.contractDate, asOfDate);

        for (RateAdjustRule rule : rules) {

            // ---------------------------------
            // SubRule이 있으면 경과연 범위 체크
            // ---------------------------------
            if (rule.hasYearRange()) {
                if (elapsedYear < rule.getYearFrom()
                        || elapsedYear > rule.getYearTo()) {
                    continue;
                }
            }

            // ---------------------------------
            // BaseRule (항상 적용)
            // ---------------------------------
            applyRule(ctx, rule.getBaseRule(), rule.getBaseAdj());

            // ---------------------------------
            // SubRule (있으면 추가 적용)
            // ---------------------------------
            applyRule(ctx, rule.getSubRule(), rule.getSubAdj());

            // VBA Exit Sub 동일
            break;
        }
    }

    // =====================================================
    // 단일 Rule 적용
    // =====================================================
    private static void applyRule(
            CalcContext ctx,
            RateAdjustRuleType ruleType,
            double adj) {

        if (ruleType == null)
            return;

        switch (ruleType) {

            case ADD:
                ctx.rateAdd += adj / 100.0;
                break;

            case SUBTRACT:
                ctx.rateAdd -= adj / 100.0;
                break;

            case MULTIPLY:
                ctx.rateMul *= adj;
                break;

            case NONE:
            default:
                // nothing
        }
    }
}
