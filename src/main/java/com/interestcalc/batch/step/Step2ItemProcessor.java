package com.interestcalc.batch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.service.Step2Service;

@Component
public class Step2ItemProcessor
        implements ItemProcessor<Step1Summary, Step2Summary> {

    private final Step2Service step2Service;
    private final CalcRunContext runCtx;

    public Step2ItemProcessor(
            Step2Service step2Service,
            CalcRunContext runCtx) {
        this.step2Service = step2Service;
        this.runCtx = runCtx;
    }

    @Override
    public Step2Summary process(Step1Summary s) {
        return step2Service.calculateOne(runCtx, s);
    }
}
