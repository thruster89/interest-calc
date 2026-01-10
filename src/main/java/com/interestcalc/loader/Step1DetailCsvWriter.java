package com.interestcalc.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.interestcalc.domain.Step1Detail;
import com.interestcalc.util.CsvUtil;

public class Step1DetailCsvWriter {

    public static void write(Path out, List<Step1Detail> rows) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {

            // ===== Header (VBA Step1_Detail 동일) =====
            bw.write(String.join(",",
                    "PLYNO", // 계약번호
                    "DEPOSIT_SEQ", // 입금회차
                    "DEPOSIT_DATE", // 입금일
                    "PRINCIPAL", // 입금액
                    "FACTOR", // 부리계수
                    "BALANCE", // 부리후 잔액
                    "STEP1_END_DATE", // STEP1 종료일
                    "DED_AMT" // 해약공제액
            ));
            bw.newLine();

            // ===== Rows =====
            for (Step1Detail r : rows) {
                bw.write(String.join(",",
                        CsvUtil.s(r.plyNo),
                        CsvUtil.i(r.depositSeq),
                        CsvUtil.d(r.depositDate),
                        CsvUtil.n(r.principal),
                        CsvUtil.n(r.factor),
                        CsvUtil.n(r.balance),
                        CsvUtil.d(r.step1EndDate),
                        CsvUtil.n(r.dedAmt)));
                bw.newLine();
            }
        }
    }
}
