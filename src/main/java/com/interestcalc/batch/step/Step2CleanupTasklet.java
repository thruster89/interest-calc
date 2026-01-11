package com.interestcalc.batch.step;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class Step2CleanupTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Value("#{jobParameters['runMode']}")
    private String jobRunMode;

    @Value("#{jobParameters['targetPlyNo']}")
    private String jobTargetPlyNo;

    @Value("${calc.run-mode}")
    private String defaultRunMode;

    @Value("${calc.target-ply-no}")
    private String defaultTargetPlyNo;

    public Step2CleanupTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) {

        String runMode = jobRunMode != null ? jobRunMode : defaultRunMode;
        String targetPlyNo = jobTargetPlyNo != null ? jobTargetPlyNo : defaultTargetPlyNo;

        if ("ALL".equals(runMode)) {
            jdbcTemplate.update("DELETE FROM STEP2_SUMMARY");
        } else {
            jdbcTemplate.update(
                    "DELETE FROM STEP2_SUMMARY WHERE PLYNO = ?",
                    targetPlyNo);
        }

        return RepeatStatus.FINISHED;
    }
}
