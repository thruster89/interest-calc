package com.interestcalc.context;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.interestcalc.domain.Contract;
import com.interestcalc.domain.Expense;
import com.interestcalc.domain.MinGuaranteedRateSegment;
import com.interestcalc.domain.RateAdjustRule;
import com.interestcalc.domain.RateSegment;
import com.interestcalc.loader.ContractCsvLoader;
import com.interestcalc.loader.ExpenseCsvLoader;
import com.interestcalc.loader.MinGuaranteedRateCsvLoader;
import com.interestcalc.loader.RateAdjustCsvLoader;
import com.interestcalc.loader.RateCsvLoader;

@Component
public class CalcMasterData {

    public final Map<String, Contract> contractMap;
    public final Map<String, List<RateSegment>> rateMap;
    public final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
    public final Map<String, List<RateAdjustRule>> rateAdjustMap;
    public final Map<String, Expense> expenseMap;

    public CalcMasterData() {

        try {
            Path dataDir = Path.of("data");

            this.contractMap = ContractCsvLoader.load(dataDir.resolve("contract.csv"));
            this.rateMap = RateCsvLoader.load(dataDir.resolve("rate.csv"));
            this.mgrMap = MinGuaranteedRateCsvLoader.load(dataDir.resolve("mgr.csv"));
            this.rateAdjustMap = RateAdjustCsvLoader.load(dataDir.resolve("rateadjust.csv"));
            this.expenseMap = ExpenseCsvLoader.load(dataDir.resolve("expense.csv"));

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load master data", e);
        }
    }
}
