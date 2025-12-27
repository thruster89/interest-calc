package com.interestcalc.app;

import java.nio.file.Path;
import java.util.Map;

import com.interestcalc.domain.Contract;
import com.interestcalc.loader.ContractCsvLoader;

public class ContractLoadTest {

    public static void main(String[] args) throws Exception {

        Map<String, Contract> map = ContractCsvLoader.load(Path.of("data/contract.csv"));

        Contract c = map.get("282120090000065");

        System.out.println(c.getPlyNo());
        System.out.println(c.getInsStartDate());
        System.out.println(c.getRateCode());
        System.out.println(c.getExpenseKey());
        System.out.println(c.getAnnuityTerm());
    }
}
