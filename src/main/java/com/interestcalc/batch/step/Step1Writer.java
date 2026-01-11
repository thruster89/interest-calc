package com.interestcalc.batch.step;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.interestcalc.batch.dto.Step1ContractResult;
import com.interestcalc.domain.Step1Detail;

@Component
public class Step1Writer implements ItemWriter<Step1ContractResult> {

    private final SqlSessionTemplate sqlSession;

    public Step1Writer(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public void write(Chunk<? extends Step1ContractResult> chunk) {

        for (Step1ContractResult r : chunk.getItems()) {

            if (r == null || r.skip)
                continue;

            for (Step1Detail d : r.details) {
                sqlSession.insert(
                        "Step1DetailMapper.insert", d);
            }

            sqlSession.insert(
                    "Step1SummaryMapper.merge", r.summary);
        }
    }
}
