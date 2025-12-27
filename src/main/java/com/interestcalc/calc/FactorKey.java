package com.interestcalc.calc;

import java.time.LocalDate;
import java.util.Objects;

public class FactorKey {

    private final String plyNo;
    private final LocalDate start;
    private final LocalDate end;

    public FactorKey(String plyNo, LocalDate start, LocalDate end) {
        this.plyNo = plyNo;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FactorKey))
            return false;
        FactorKey that = (FactorKey) o;
        return Objects.equals(plyNo, that.plyNo)
                && Objects.equals(start, that.start)
                && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plyNo, start, end);
    }
}
