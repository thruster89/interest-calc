package com.interestcalc.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.interestcalc.domain.Contract;

/**
 * Contract CSV Loader
 *
 * CSV columns (0-based):
 * 0 PLYNO
 * 1 INS_ST
 * 2 GDCD
 * 3 RATE_CODE
 * 4 INS_CLSTR
 * 5 RL_PYM_TRM
 * 6 DFR_TRM
 * 7 AN_PY_STDT
 * 8 EXPCT_INRT
 * 9 PR_NWCRT_TAMT
 * 10 CRFW_PR_NWCRT_TAMT
 * 11 PR_BZCS_DSCNO
 * 12 PYM_CYCCD
 * 13 AN_INS_TRM_FLGCD
 * 14 AN_PYTCD
 * 15 AN_PY_GIRT
 * 16 AN_PY_TRM
 */
public class ContractCsvLoader {

    public static Map<String, Contract> load(Path csvPath) throws IOException {

        Map<String, Contract> map = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] c = line.split(",", -1);

                String plyNo = c[0];
                LocalDate insSt = LocalDate.parse(c[1]);
                String gdcd = c[2];
                String rateCode = c[3];
                LocalDate insClstr = LocalDate.parse(c[4]);

                int rlPymTrm = parseInt(c[5]);
                int dfrTrm = parseInt(c[6]);

                LocalDate annPyStdt = LocalDate.parse(c[7]);
                double expctInrt = parseDouble(c[8]);

                double prNwcrtTamt = parseDouble(c[9]);
                double crfwPrNwcrtTamt = parseDouble(c[10]);

                String expenseKey = c[11];
                String pymCyccd = c[12];
                String annInsTrmFlgcd = c[13];
                String annPytcd = c[14];
                int annPyGirt = parseInt(c[15]);
                int annPyTrm = parseInt(c[16]);

                Contract contract = new Contract(
                        plyNo,
                        insSt,
                        gdcd,
                        rateCode,
                        insClstr,
                        rlPymTrm,
                        dfrTrm,
                        annPyStdt,
                        expctInrt,
                        prNwcrtTamt,
                        crfwPrNwcrtTamt,
                        expenseKey,
                        pymCyccd,
                        annInsTrmFlgcd,
                        annPytcd,
                        annPyGirt,
                        annPyTrm);

                map.put(plyNo, contract);
            }
        }

        return map;
    }

    private static int parseInt(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
    }

    private static double parseDouble(String s) {
        return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s.trim());
    }
}
