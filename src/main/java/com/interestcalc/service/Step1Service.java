package com.interestcalc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.interestcalc.batch.dto.DepositJoinRow;
import com.interestcalc.batch.dto.Step1ContractResult;
import com.interestcalc.calc.CalcBaseDateResolver;
import com.interestcalc.calc.CalcFactorByYear;
import com.interestcalc.calc.DeductionCalculator;
import com.interestcalc.context.CalcContext;
import com.interestcalc.context.CalcMasterData;
import com.interestcalc.context.CalcRunContext;
import com.interestcalc.domain.Step1Detail;
import com.interestcalc.domain.Step1Summary;

@Service
public class Step1Service {

        private final CalcMasterData master;

        public Step1Service(CalcMasterData master) {
                this.master = master;
        }

        public Step1ContractResult calculateContract(
                        CalcRunContext runCtx,
                        List<DepositJoinRow> rows) {

                DepositJoinRow first = rows.get(0);

                LocalDate baseDate = CalcBaseDateResolver.resolve(runCtx, first.getInsStartDate());

                LocalDate payEndDate = first.getInsStartDate().plusYears(first.getPayYears());

                LocalDate step1EndDate = baseDate.isBefore(payEndDate) ? baseDate : payEndDate;

                CalcContext ctx = new CalcContext();
                ctx.plyNo = first.getPlyNo();
                ctx.contractDate = first.getInsStartDate();
                ctx.rateArr = master.rateMap.get(first.getRateCode());
                ctx.mgrArr = master.mgrMap.get(first.getProductCode());
                ctx.rateAdjustRules = master.rateAdjustMap.get(first.getRateCode());
                ctx.debugMode = runCtx.debugMode;
                ctx.applyTag = "STEP1";

                double totalBalance = 0.0;
                double lastDeductAmt = 0.0;

                List<Step1Detail> details = new ArrayList<>();

                for (DepositJoinRow row : rows) {

                        if (row.getDepositDate().isAfter(step1EndDate)) {
                                continue;
                        }

                        ctx.depositSeq = row.getDepositSeq();
                        ctx.principal = row.getPrincipal();

                        double factor = CalcFactorByYear.calc(
                                        ctx,
                                        row.getDepositDate(),
                                        step1EndDate);

                        double balance = row.getPrincipal() * factor;

                        double dedAmt = DeductionCalculator.calc(
                                        row.getPymCyccd(),
                                        row.getPayYears(),
                                        row.getDepositSeq(),
                                        row.getDeductible());

                        totalBalance += balance;
                        lastDeductAmt = dedAmt;

                        details.add(new Step1Detail(
                                        row.getPlyNo(),
                                        row.getDepositSeq(),
                                        row.getDepositDate(),
                                        row.getPrincipal(),
                                        factor,
                                        balance,
                                        step1EndDate,
                                        dedAmt));
                }

                Step1Summary summary = Step1Summary.fromContract(first.toContract(), step1EndDate);

                summary.applyResult(totalBalance, lastDeductAmt);

                return new Step1ContractResult(details, summary);
        }
}
