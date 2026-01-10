package com.interestcalc.test;

import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Deposit;
import com.interestcalc.loader.DepositCsvLoader;

public class DepositLoadTest {

    public static void main(String[] args) throws Exception {

        Path path = Path.of("data/deposit.csv");

        List<Deposit> deposits = DepositCsvLoader.load(path);

        for (Deposit d : deposits) {
            System.out.println(d);
        }
    }
}
