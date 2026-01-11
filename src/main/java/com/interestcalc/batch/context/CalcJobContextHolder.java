package com.interestcalc.batch.context;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

public final class CalcJobContextHolder {

    private static final String KEY = "calcJobContext";

    private CalcJobContextHolder() {
    }

    public static CalcJobContext getOrCreate(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext ctx = jobExecution.getExecutionContext();

        CalcJobContext data = (CalcJobContext) ctx.get(KEY);
        if (data == null) {
            data = new CalcJobContext();
            ctx.put(KEY, data);
        }
        return data;
    }
}
