package com.interestcalc.batch.dto;

import java.util.List;

import com.interestcalc.domain.Step1Detail;
import com.interestcalc.domain.Step1Summary;

public class Step1ContractResult {

    public final List<Step1Detail> details;
    public final Step1Summary summary;
    public final boolean skip;

    public Step1ContractResult(
            List<Step1Detail> details,
            Step1Summary summary) {

        this.details = details;
        this.summary = summary;
        this.skip = false;
    }

    public static Step1ContractResult skip() {
        Step1ContractResult r = new Step1ContractResult(null, null);
        return new Step1ContractResult(null, null, true);
    }

    private Step1ContractResult(
            List<Step1Detail> details,
            Step1Summary summary,
            boolean skip) {

        this.details = details;
        this.summary = summary;
        this.skip = skip;
    }
}
