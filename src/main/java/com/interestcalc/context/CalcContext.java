package com.interestcalc.context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.interestcalc.domain.CalcDebugRow;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;

/**
 * 계산 공용 Context
 * VBA calcContext 1:1 대응 (상태 보관 전용)
 */
public class CalcContext {

    /*
     * =========================
     * 식별 정보
     * =========================
     */
    public String plyNo;
    public int depositSeq;
    public LocalDate contractDate;

    /*
     * =========================
     * 금액
     * =========================
     */
    public double principal;

    /*
     * =========================
     * 이율 데이터
     * =========================
     */
    public List<RateSegment> rateArr;
    public List<MinGuaranteedRateSegment> mgrArr;
    public List<RateAdjustRule> rateAdjustRules;

    /*
     * =========================
     * 이율 조정 상태 (세그먼트별)
     * =========================
     */
    public double rateAdd = 0.0; // +/-
    public double rateMul = 1.0; // ×

    /*
     * =========================
     * 연 단위 추적
     * =========================
     */
    public int yearIdx = 1;
    public boolean isFirstSegInYear = true;

    /*
     * =========================
     * 디버그
     * =========================
     */
    public boolean debugMode = false;
    public String applyTag;
    public List<CalcDebugRow> debugRows = new ArrayList<>();

    /*
     * =========================
     * 생성자
     * =========================
     */
    public CalcContext() {
        // 명시적 초기화
        this.rateAdd = 0.0;
        this.rateMul = 1.0;
    }
}
