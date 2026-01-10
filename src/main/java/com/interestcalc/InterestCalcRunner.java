package com.interestcalc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.interestcalc.app.InterestCalcMain;

@Component
public class InterestCalcRunner implements CommandLineRunner {

    private final InterestCalcMain main;

    public InterestCalcRunner(InterestCalcMain main) {
        this.main = main;
    }

    @Override
    public void run(String... args) throws Exception {
        main.run();
    }
}
