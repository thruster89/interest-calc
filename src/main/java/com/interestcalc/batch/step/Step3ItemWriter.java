package com.interestcalc.batch.step;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.interestcalc.domain.Step3Summary;

@Component
public class Step3ItemWriter implements ItemWriter<Step3Summary> {

    private final SqlSessionTemplate sqlSession;

    public Step3ItemWriter(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void write(Chunk<? extends Step3Summary> chunk) {

        for (Step3Summary s : chunk.getItems()) {
            if (s == null)
                continue;

            sqlSession.insert(
                    "Step3SummaryMapper.insert",
                    s);
        }
    }
}
