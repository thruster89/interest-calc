package com.interestcalc.context;

import java.time.LocalDate;
import java.util.List;

import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateSegment;

/**
 * 계산 공용 Context
 * VBA calcContext 1:1 대응
 */
public class CalcContext {

    /*
     * =========================
     * 식별 정보
     * =========================
     */
    public String plyNo; // 증권번호
    public long depositSeq; // 입금순번 (Step1)
    public LocalDate contractDate; // 계약일자

    /*
     * =========================
     * 원금 / 잔액
     * =========================
     */
    public double principal; // 계산 기준 원금

    /*
     * =========================
     * 이율 정보
     * =========================
     */
    public List<RateSegment> rateArr; // 기준이율 구간
    public List<MinGuaranteedRateSegment> mgrArr; // 최저보증이율 구간
    public double rateAdj; // 가산/차감 이율 (기본 0)
    public double rateMul = 1.0; // 배율
    public double rateAdd = 0.0; // 가감
    /*
     * =========================
     * 디버그 제어
     * =========================
     */
    public boolean debugMode = false;
    public int yearIdx = 1;
    public boolean isFirstSegInYear = true;

    /*
     * =========================
     * 생성자
     * =========================
     */
    public CalcContext() {
        this.rateAdj = 0.0;
        this.rateMul = 1.0;
        this.rateAdd = 0.0;
    }

    /*
     * =========================
     * 기준이율 조회
     * =========================
     */
    public double resolveBaseRate(LocalDate date) {

        if (rateArr == null || rateArr.isEmpty()) {
            throw new IllegalStateException("rateArr is empty");
        }

        for (RateSegment seg : rateArr) {
            if (seg.contains(date)) {
                double base = seg.getRate();
                // 🔴 기존
                // return base + rateAdj;

                // 🟢 변경: 곱 → 더하기
                return base * rateMul + rateAdd;
            }
        }

        throw new IllegalStateException(
                "Base rate not found for date: " + date);
    }

    /*
     * =========================
     * 최저보증이율 조회
     * =========================
     */
    public double resolveMgrRate(int elapsedYear) {

        if (mgrArr == null || mgrArr.isEmpty()) {
            return 0.0;
        }

        for (MinGuaranteedRateSegment seg : mgrArr) {
            if (seg.matches(elapsedYear)) {
                return seg.getRate();
            }
        }

        return 0.0;
    }

    /*
     * =========================
     * 적용이율 (max)
     * =========================
     */
    public double resolveAppliedRate(LocalDate date, int elapsedYear) {

        double baseRate = resolveBaseRate(date);
        double mgrRate = resolveMgrRate(elapsedYear);

        return Math.max(baseRate, mgrRate);
    }
}
