package com.interestcalc.batch.context;

import java.io.Serializable;
import java.util.List;

import com.interestcalc.domain.Deposit;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;

public class CalcJobContext implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== Input =====
    private List<Deposit> deposits;

    // ===== Step outputs =====
    private List<Step1Summary> step1Summaries;
    private List<Step2Summary> step2Summaries;

    // ===== getters / setters =====
    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }

    public List<Step1Summary> getStep1Summaries() {
        return step1Summaries;
    }

    public void setStep1Summaries(List<Step1Summary> step1Summaries) {
        this.step1Summaries = step1Summaries;
    }

    public List<Step2Summary> getStep2Summaries() {
        return step2Summaries;
    }

    public void setStep2Summaries(List<Step2Summary> step2Summaries) {
        this.step2Summaries = step2Summaries;
    }
}
