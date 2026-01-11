package com.interestcalc.batch.step;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.interestcalc.domain.Step3Summary;

@Component
@StepScope
public class Step3SummaryItemWriter implements ItemWriter<Step3Summary> {

    private final JdbcTemplate jdbcTemplate;

    public Step3SummaryItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =============================
    // Batch buffer
    // =============================
    private final List<Object[]> batch = new ArrayList<>();

    // =============================
    // SQL
    // =============================
    private static final String INSERT_SQL = """
                INSERT INTO STEP3_SUMMARY (
                    PLYNO,
                    BALANCE,
                    CALC_BASE_DATE,
                    RATE_CODE,
                    PRODUCT_CODE,
                    EXPENSE_KEY,
                    ANNUITY_TERM
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    // =============================
    // WRITE
    // =============================
    @Override
    public void write(Chunk<? extends Step3Summary> chunk) {

        for (Step3Summary s : chunk.getItems()) {

            batch.add(new Object[] {
                    s.plyNo(),
                    s.balance(),
                    Date.valueOf(s.calcBaseDate()),
                    s.rateCode(),
                    s.productCode(),
                    s.expenseKey(),
                    s.annuityTerm()
            });
        }

        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT_SQL, batch);
            batch.clear();
        }
    }
}
