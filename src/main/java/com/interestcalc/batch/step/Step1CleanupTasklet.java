package com.interestcalc.batch.step;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Step1CleanupTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Value("#{jobParameters['runMode']}")
    private String jobRunMode;

    @Value("#{jobParameters['targetPlyNo']}")
    private String jobTargetPlyNo;

    public Step1CleanupTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) {

        if ("ALL".equalsIgnoreCase(jobRunMode)) {
            jdbcTemplate.update("DELETE FROM STEP1_DETAIL");
            jdbcTemplate.update("DELETE FROM STEP1_SUMMARY");
        } else {
            jdbcTemplate.update(
                    "DELETE FROM STEP1_DETAIL WHERE PLYNO = ?",
                    jobTargetPlyNo);
            jdbcTemplate.update(
                    "DELETE FROM STEP1_SUMMARY WHERE PLYNO = ?",
                    jobTargetPlyNo);
        }
        return RepeatStatus.FINISHED;
    }
}
