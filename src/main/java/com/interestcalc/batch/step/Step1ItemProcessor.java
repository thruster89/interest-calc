package com.interestcalc.batch.step;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.interestcalc.batch.dto.DepositJoinRow;
import com.interestcalc.batch.dto.Step1ContractResult;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.service.Step1Service;

/**
 * Step1 계약 단위 aggregation Processor
 *
 * 입력 : DepositJoinRow (정렬: PLYNO, DEPOSIT_SEQ)
 * 출력 : Step1ContractResult (계약 1건)
 *
 * 마지막 계약 처리는 Sentinel Row("__END__")로 flush
 */
@Component
public class Step1ItemProcessor
        implements ItemProcessor<DepositJoinRow, Step1ContractResult> {

    private final Step1Service step1Service;
    private final CalcRunContext runCtx;

    // ===========================
    // aggregation state
    // ===========================
    private String currentPlyNo = null;
    private final List<DepositJoinRow> buffer = new ArrayList<>();

    public Step1ItemProcessor(
            Step1Service step1Service,
            CalcRunContext runCtx) {

        this.step1Service = step1Service;
        this.runCtx = runCtx;
    }

    @Override
    public Step1ContractResult process(DepositJoinRow row) {

        // ==========================================
        // 1. Sentinel row → 마지막 계약 flush
        // ==========================================
        if ("__END__".equals(row.getPlyNo())) {

            if (buffer.isEmpty()) {
                return null;
            }

            Step1ContractResult last = step1Service.calculateContract(runCtx, buffer);

            buffer.clear();
            currentPlyNo = null;

            return last;
        }

        // ==========================================
        // 2. 첫 row
        // ==========================================
        if (currentPlyNo == null) {
            currentPlyNo = row.getPlyNo();
            buffer.add(row);
            return null;
        }

        // ==========================================
        // 3. 동일 계약 → 계속 적재
        // ==========================================
        if (currentPlyNo.equals(row.getPlyNo())) {
            buffer.add(row);
            return null;
        }

        // ==========================================
        // 4. 계약 변경 → 이전 계약 flush
        // ==========================================
        Step1ContractResult result = step1Service.calculateContract(runCtx, buffer);

        buffer.clear();
        currentPlyNo = row.getPlyNo();
        buffer.add(row);

        return result;
    }
}
