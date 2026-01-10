package com.interestcalc.context;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

public class CalcMasterData {

    public final Map<String, Contract> contractMap;
    public final Map<String, List<RateSegment>> rateMap;
    public final Map<String, List<MinGuaranteedRateSegment>> mgrMap;
    public final Map<String, List<RateAdjustRule>> rateAdjustMap;
    public final Map<String, Expense> expenseMap;

    private CalcMasterData(
            Map<String, Contract> contractMap,
            Map<String, List<RateSegment>> rateMap,
            Map<String, List<MinGuaranteedRateSegment>> mgrMap,
            Map<String, List<RateAdjustRule>> rateAdjustMap,
            Map<String, Expense> expenseMap) {

        this.contractMap = contractMap;
        this.rateMap = rateMap;
        this.mgrMap = mgrMap;
        this.rateAdjustMap = rateAdjustMap;
        this.expenseMap = expenseMap;
    }

    public static CalcMasterData loadAll(Path dataDir) throws Exception {

        return new CalcMasterData(
                ContractCsvLoader.load(dataDir.resolve("contract.csv")),
                RateCsvLoader.load(dataDir.resolve("rate.csv")),
                MinGuaranteedRateCsvLoader.load(dataDir.resolve("mgr.csv")),
                RateAdjustCsvLoader.load(dataDir.resolve("rateadjust.csv")),
                ExpenseCsvLoader.load(dataDir.resolve("expense.csv")));
    }
}
