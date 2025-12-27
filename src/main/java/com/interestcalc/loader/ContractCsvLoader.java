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
 * CSV columns (header):
 * PLYNO,
 * INS_ST,
 * GDCD,
 * RATE_CODE,
 * INS_CLSTR,
 * RL_PYM_TRM,
 * DFR_TRM,
 * AN_PY_STDT,
 * PR_BZCS_DSCNO,
 * AN_PY_TRM
 */
public class ContractCsvLoader {

    /**
     * @return Map<PLYNO, Contract>
     */
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

                String[] t = line.split(",", -1);

                /*
                 * CSV index mapping (0-based)
                 * 0 PLYNO
                 * 1 INS_ST
                 * 2 GDCD
                 * 3 RATE_CODE
                 * 4 INS_CLSTR
                 * 5 RL_PYM_TRM
                 * 6 DFR_TRM
                 * 7 AN_PY_STDT
                 * 8 PR_BZCS_DSCNO
                 * 9 AN_PY_TRM
                 */

                String plyNo = t[0].trim();

                LocalDate insStartDate = LocalDate.parse(t[1].trim());
                String productCode = t[2].trim();
                String rateCode = t[3].trim();
                LocalDate insEndDate = LocalDate.parse(t[4].trim());

                int payYears = parseInt(t[5]);
                int deferYears = parseInt(t[6]);

                LocalDate annuityDate = LocalDate.parse(t[7].trim());
                String expenseKey = t[11].trim();
                int annuityTerm = parseInt(t[16]);

                Contract c = new Contract(
                        plyNo,
                        insStartDate,
                        insEndDate,
                        productCode,
                        rateCode,
                        payYears,
                        deferYears,
                        annuityDate,
                        annuityTerm,
                        expenseKey);

                map.put(plyNo, c);
            }
        }

        return map;
    }

    private static int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(s.trim());
    }
}
