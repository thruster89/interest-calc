package com.interestcalc.batch.step;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.interestcalc.domain.Step2Summary;

@Configuration
public class Step3ItemReaderConfig {

        @Bean
        @StepScope
        public MyBatisCursorItemReader<Step2Summary> step3ItemReader(
                        SqlSessionFactory sqlSessionFactory,

                        @Value("#{jobParameters['runMode']}") String jobRunMode,
                        @Value("#{jobParameters['targetPlyNo']}") String jobTargetPlyNo,

                        @Value("${calc.run-mode}") String defaultRunMode,
                        @Value("${calc.target-ply-no}") String defaultTargetPlyNo) {

                String runMode = jobRunMode != null ? jobRunMode : defaultRunMode;
                String targetPlyNo = jobTargetPlyNo != null ? jobTargetPlyNo : defaultTargetPlyNo;

                MyBatisCursorItemReader<Step2Summary> reader = new MyBatisCursorItemReader<>();

                reader.setSqlSessionFactory(sqlSessionFactory);

                // ⚠️ XML namespace와 반드시 일치해야 함
                reader.setQueryId(
                                "com.interestcalc.mapper.DepositMapper.selectStep2Summary");

                reader.setParameterValues(Map.of(
                                "runMode", runMode,
                                "targetPlyNo", targetPlyNo));

                return reader;
        }
}
