package com.interestcalc.batch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.domain.Step3Summary;
import com.interestcalc.service.Step3Service;

@Component
public class Step3ItemProcessor
        implements ItemProcessor<Step2Summary, Step3Summary> {

    private final Step3Service service;
    private final CalcRunContext runCtx;

    public Step3ItemProcessor(
            Step3Service service,
            CalcRunContext runCtx) {
        this.service = service;
        this.runCtx = runCtx;
    }

    @Override
    public Step3Summary process(Step2Summary item) {
        return service.calculateOne(runCtx, item);
    }
}
