package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step2ExpDetail;
import com.interestcalc.util.CsvUtil;

public class Step2ExpDetailCsvWriter {

    public static void write(Path path, List<Step2ExpDetail> list) throws Exception {

        try (BufferedWriter w = Files.newBufferedWriter(path)) {

            w.write("PLYNO,SEQ,FROM_DATE,EXP_AMT,FACTOR,ACC_AMT,STEP2_END");
            w.newLine();

            for (Step2ExpDetail d : list) {
                w.write(String.join(",",
                        d.plyNo,
                        String.valueOf(d.seq),
                        CsvUtil.d(d.fromDate),
                        CsvUtil.n(d.expenseAmt),
                        CsvUtil.n(d.factor),
                        CsvUtil.n(d.accAmount),
                        CsvUtil.d(d.step2EndDate)));
                w.newLine();
            }
        }
    }
}
