-- 대량 INSERT 전에
ALTER SESSION SET nls_date_format = 'YYYY-MM-DD';

-- redo/undo 부담 줄이기 (테스트 환경)
ALTER TABLE CONTRACT NOLOGGING;
ALTER TABLE DEPOSIT NOLOGGING;

INSERT /*+ APPEND */ INTO CONTRACT (
    PLYNO,
    INS_ST,
    INS_CLSTR,
    AN_PY_STDT,
    GDCD,
    RATE_CODE,
    PR_BZCS_DSCNO,
    RL_PYM_TRM,
    DFR_TRM,
    AN_PY_TRM,
    EXPCT_INRT,
    PR_NWCRT_TAMT,
    CRFW_PR_NWCRT_TAMT,
    PYM_CYCCD,
    AN_INS_TRM_FLGCD,
    AN_PYTCD,
    AN_PY_GIRT
)
SELECT
    'C' || LPAD(LEVEL, 8, '0')                                   AS PLYNO,
    DATE '2000-01-01' + TRUNC(DBMS_RANDOM.VALUE(0, 7300))       AS INS_ST,
    DATE '2000-01-01' + TRUNC(DBMS_RANDOM.VALUE(8000, 15000))   AS INS_CLSTR,
    DATE '2000-01-01' + TRUNC(DBMS_RANDOM.VALUE(6000, 10000))   AS AN_PY_STDT,
    'P' || MOD(LEVEL, 50)                                       AS GDCD,
    'R' || MOD(LEVEL, 20)                                       AS RATE_CODE,
    'E' || MOD(LEVEL, 10)                                       AS PR_BZCS_DSCNO,
    TRUNC(DBMS_RANDOM.VALUE(5, 21))                              AS RL_PYM_TRM,
    TRUNC(DBMS_RANDOM.VALUE(0, 6))                               AS DFR_TRM,
    TRUNC(DBMS_RANDOM.VALUE(10, 31))                             AS AN_PY_TRM,
    ROUND(DBMS_RANDOM.VALUE(0.015, 0.045), 6)                   AS EXPCT_INRT,
    ROUND(DBMS_RANDOM.VALUE(0, 500000), 2)                       AS PR_NWCRT_TAMT,
    ROUND(DBMS_RANDOM.VALUE(0, 300000), 2)                       AS CRFW_PR_NWCRT_TAMT,
    'Y'                                                         AS PYM_CYCCD,
    'A'                                                         AS AN_INS_TRM_FLGCD,
    'M'                                                         AS AN_PYTCD,
    MOD(LEVEL, 12) + 1                                          AS AN_PY_GIRT
FROM dual
CONNECT BY LEVEL <= 100000;

COMMIT;


INSERT /*+ APPEND */ INTO DEPOSIT (
    PLYNO,
    DEPOSIT_SEQ,
    DEPOSIT_DATE,
    PRINCIPAL
)
SELECT
    c.PLYNO,
    d.SEQ                                     AS DEPOSIT_SEQ,
    ADD_MONTHS(c.INS_ST, d.SEQ - 1)           AS DEPOSIT_DATE,
    ROUND(DBMS_RANDOM.VALUE(100000, 2000000), 2) AS PRINCIPAL
FROM (
    SELECT
        PLYNO,
        INS_ST,
        TRUNC(DBMS_RANDOM.VALUE(100, 361)) AS DEPOSIT_CNT
    FROM CONTRACT
) c
JOIN (
    SELECT LEVEL AS SEQ
    FROM dual
    CONNECT BY LEVEL <= 360
) d
  ON d.SEQ <= c.DEPOSIT_CNT;

COMMIT;
