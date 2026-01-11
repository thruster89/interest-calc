package com.interestcalc.batch.step;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.interestcalc.domain.Step2Summary;

@Component
public class Step2ItemWriter implements ItemWriter<Step2Summary> {

    private final SqlSessionTemplate sqlSession;

    public Step2ItemWriter(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void write(Chunk<? extends Step2Summary> chunk) {

        for (Step2Summary s : chunk.getItems()) {

            if (s == null)
                continue;

            sqlSession.insert(
                    "Step2SummaryMapper.insert",
                    s);
        }
    }
}
