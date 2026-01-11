package com.interestcalc.batch;

import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.interestcalc.batch.dto.DepositJoinRow;
import com.interestcalc.batch.dto.Step1ContractResult;
import com.interestcalc.batch.step.Step1CleanupTasklet;
import com.interestcalc.batch.step.Step1ItemProcessor;
import com.interestcalc.batch.step.Step1Writer;
import com.interestcalc.batch.step.Step2CleanupTasklet;
import com.interestcalc.batch.step.Step2ItemProcessor;
import com.interestcalc.batch.step.Step2ItemWriter;
import com.interestcalc.batch.step.Step3CleanupTasklet;
import com.interestcalc.batch.step.Step3ItemProcessor;
import com.interestcalc.batch.step.Step3ItemWriter;
import com.interestcalc.domain.Step1Summary;
import com.interestcalc.domain.Step2Summary;
import com.interestcalc.domain.Step3Summary;

@Configuration
public class InterestCalcBatchConfig {

        private static final int CHUNK_SIZE = 100;

        // =====================================================
        // JOB
        // =====================================================
        @Bean
        public Job interestCalcJob(
                        JobRepository repo,

                        Step step1CleanupStep,
                        Step step1Step,

                        Step step2CleanupStep,
                        Step step2Step,

                        Step step3CleanupStep,
                        Step step3Step) {

                return new JobBuilder("interestCalcJob", repo)
                                .start(step1CleanupStep)
                                .next(step1Step)

                                .next(step2CleanupStep)
                                .next(step2Step)

                                .next(step3CleanupStep)
                                .next(step3Step)

                                .build();
        }

        // =====================================================
        // STEP 1 : CLEANUP
        // =====================================================
        @Bean
        public Step step1CleanupStep(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        Step1CleanupTasklet tasklet) {

                return new StepBuilder("step1CleanupStep", repo)
                                .tasklet(tasklet, tx)
                                .build();
        }

        // =====================================================
        // STEP 1 : DETAIL + SUMMARY (MERGE)
        // DepositJoinRow → Step1ResultBundle
        // =====================================================
        @Bean
        public Step step1Step(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        MyBatisCursorItemReader<DepositJoinRow> step1ItemReader,
                        Step1ItemProcessor step1ItemProcessor,
                        Step1Writer step1Writer) {

                return new StepBuilder("step1Step", repo)
                                .<DepositJoinRow, Step1ContractResult>chunk(100, tx)
                                .reader(step1ItemReader)
                                .processor(step1ItemProcessor)
                                .writer(step1Writer)
                                .build();
        }

        // =====================================================
        // STEP 2 : CLEANUP
        // =====================================================
        @Bean
        public Step step2CleanupStep(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        Step2CleanupTasklet tasklet) {

                return new StepBuilder("step2CleanupStep", repo)
                                .tasklet(tasklet, tx)
                                .build();
        }

        // =====================================================
        // STEP 2 : SUMMARY 계산
        // Step1Summary → Step2Summary
        // =====================================================
        @Bean
        public Step step2Step(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        MyBatisCursorItemReader<Step1Summary> step2ItemReader,
                        Step2ItemProcessor step2ItemProcessor,
                        Step2ItemWriter step2Writer) {

                return new StepBuilder("step2Step", repo)
                                .<Step1Summary, Step2Summary>chunk(CHUNK_SIZE, tx)
                                .reader(step2ItemReader)
                                .processor(step2ItemProcessor)
                                .writer(step2Writer)
                                .build();
        }

        // =====================================================
        // STEP 3 : CLEANUP
        // =====================================================
        @Bean
        public Step step3CleanupStep(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        Step3CleanupTasklet tasklet) {

                return new StepBuilder("step3CleanupStep", repo)
                                .tasklet(tasklet, tx)
                                .build();
        }

        // =====================================================
        // STEP 3 : FINAL SUMMARY
        // Step2Summary → Step3Summary
        // =====================================================
        @Bean
        public Step step3Step(
                        JobRepository repo,
                        PlatformTransactionManager tx,
                        MyBatisCursorItemReader<Step2Summary> step3ItemReader,
                        Step3ItemProcessor step3ItemProcessor,
                        Step3ItemWriter step3Writer) {

                return new StepBuilder("step3Step", repo)
                                .<Step2Summary, Step3Summary>chunk(CHUNK_SIZE, tx)
                                .reader(step3ItemReader)
                                .processor(step3ItemProcessor)
                                .writer(step3Writer)
                                .build();
        }
}
