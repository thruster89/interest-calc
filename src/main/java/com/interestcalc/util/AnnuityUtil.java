package com.interestcalc.util;

public class AnnuityUtil {

    /**
     * VBA CalcAnnuityAmount 이식
     *
     * reserve : 현재 적립금
     * v : 할인율 (1 / (1 + rate))
     * remainYears : 잔여 연수
     *
     * VBA:
     * sumV = Σ v^i (i = 0 .. remainYears-1)
     * annuity = reserve / sumV
     */
    public static double calcAnnuityAmount(
            double reserve,
            double v,
            int remainYears) {

        if (reserve <= 0.0 || remainYears <= 0) {
            return 0.0;
        }

        double sumV = 0.0;
        for (int i = 0; i < remainYears; i++) {
            sumV += Math.pow(v, i);
        }

        if (sumV == 0.0) {
            return 0.0;
        }

        return reserve / sumV;
    }
}
