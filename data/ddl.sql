CREATE TABLE CONTRACT (
    -- =========================
    -- KEY
    -- =========================
    PLYNO               VARCHAR2(30)   NOT NULL,

    -- =========================
    -- Dates
    -- =========================
    INS_ST              DATE           NOT NULL, -- 보험개시일
    INS_CLSTR           DATE           NOT NULL, -- 보험종료일
    AN_PY_STDT          DATE           NOT NULL, -- 연금개시일

    -- =========================
    -- Codes
    -- =========================
    GDCD                VARCHAR2(20)   NOT NULL, -- 상품코드
    RATE_CODE           VARCHAR2(20)   NOT NULL, -- 이율코드
    PR_BZCS_DSCNO       VARCHAR2(20)   NOT NULL, -- 사업비키

    -- =========================
    -- Terms
    -- =========================
    RL_PYM_TRM          NUMBER(5)      NOT NULL, -- 납입기간(년)
    DFR_TRM             NUMBER(5)      NOT NULL, -- 거치기간
    AN_PY_TRM           NUMBER(5)      NOT NULL, -- 연금지급기간

    -- =========================
    -- Amounts / Rates
    -- =========================
    EXPCT_INRT          NUMBER(9,6),              -- 기대이율
    PR_NWCRT_TAMT       NUMBER(18,2),             -- 공제금액
    CRFW_PR_NWCRT_TAMT  NUMBER(18,2),             -- 이월공제금액

    -- =========================
    -- Flags / Codes
    -- =========================
    PYM_CYCCD           VARCHAR2(10),
    AN_INS_TRM_FLGCD    VARCHAR2(10),
    AN_PYTCD            VARCHAR2(10),
    AN_PY_GIRT          NUMBER(5),

    -- =========================
    -- Audit (권장)
    -- =========================
    REG_DTM             DATE DEFAULT SYSDATE,
    UPD_DTM             DATE
);

ALTER TABLE CONTRACT
ADD CONSTRAINT PK_CONTRACT
PRIMARY KEY (PLYNO);


CREATE TABLE DEPOSIT (
    -- =========================
    -- KEY
    -- =========================
    PLYNO           VARCHAR2(30)   NOT NULL, -- 계약번호
    DEPOSIT_SEQ     NUMBER(10)     NOT NULL, -- 입금회차 (계약 내 순번)

    -- =========================
    -- Deposit Info
    -- =========================
    DEPOSIT_DATE    DATE           NOT NULL, -- 입금일
    PRINCIPAL       NUMBER(18,2)   NOT NULL, -- 입금액

    -- =========================
    -- Audit (권장)
    -- =========================
    REG_DTM         DATE DEFAULT SYSDATE,
    UPD_DTM         DATE
);

ALTER TABLE DEPOSIT
ADD CONSTRAINT PK_DEPOSIT
PRIMARY KEY (PLYNO, DEPOSIT_SEQ);


DROP TABLE STEP1_SUMMARY PURGE;

CREATE TABLE STEP1_SUMMARY (
    PLYNO              VARCHAR2(30)   NOT NULL,
    NET_BALANCE         NUMBER(18, 6)  NOT NULL,
    STEP1_END_DATE      DATE           NOT NULL,
    ANNUITY_DATE        DATE,
    INS_START_DATE      DATE           NOT NULL,
    RATE_CODE           VARCHAR2(20),
    PRODUCT_CODE        VARCHAR2(20),
    EXPENSE_KEY         VARCHAR2(30),
    ANNUITY_TERM        NUMBER(5),
    INS_END_DATE        DATE,
    TOTAL_BALANCE       NUMBER(18, 6)  NOT NULL,
    DEDUCT_AMT_LAST     NUMBER(18, 6)  NOT NULL,
    CREATED_AT          DATE DEFAULT SYSDATE,
    CONSTRAINT PK_STEP1_SUMMARY
        PRIMARY KEY (PLYNO)
);
CREATE TABLE STEP1_DETAIL (
    PLYNO           VARCHAR2(20)   NOT NULL,
    DEPOSIT_SEQ     NUMBER         NOT NULL,
    DEPOSIT_DATE    DATE           NOT NULL,

    PRINCIPAL       NUMBER(18, 6)   NOT NULL,
    FACTOR          NUMBER(18, 10)  NOT NULL,
    BALANCE         NUMBER(18, 6)   NOT NULL,

    STEP1_END_DATE  DATE           NOT NULL,
    DEDUCT_AMT      NUMBER(18, 6)   NOT NULL,

    CREATED_AT      DATE DEFAULT SYSDATE
);

-- 조회/삭제 성능용
CREATE INDEX IDX_STEP1_DETAIL_01
    ON STEP1_DETAIL (PLYNO);

CREATE INDEX IDX_STEP1_DETAIL_02
    ON STEP1_DETAIL (PLYNO, DEPOSIT_SEQ);

    
CREATE TABLE STEP2_SUMMARY (
    PLYNO                 VARCHAR2(20)   NOT NULL,
    BALANCE                NUMBER(18, 6)  NOT NULL,
    STEP2_END_DATE          DATE           NOT NULL,
    ANNUITY_DATE            DATE           NOT NULL,
    RATE_CODE               VARCHAR2(20)   NOT NULL,
    PRODUCT_CODE            VARCHAR2(20)   NOT NULL,
    EXPENSE_KEY             VARCHAR2(20)   NOT NULL,
    ANNUITY_TERM            NUMBER(5)      NOT NULL,
    INS_END_DATE            DATE           NOT NULL,
    CONTRACT_DATE           DATE           NOT NULL,
    BASE_END_BALANCE        NUMBER(18, 6)  NOT NULL,
    MONTHLY_EXP_ACC         NUMBER(18, 6)  NOT NULL,
    CONSTRAINT PK_STEP2_SUMMARY
        PRIMARY KEY (PLYNO)
);

CREATE TABLE STEP3_SUMMARY (
    PLYNO          VARCHAR2(30)   NOT NULL,
    BALANCE         NUMBER(18,6)   NOT NULL,
    CALC_BASE_DATE  DATE           NOT NULL,
    RATE_CODE       VARCHAR2(20),
    PRODUCT_CODE    VARCHAR2(20),
    EXPENSE_KEY     VARCHAR2(30),
    ANNUITY_TERM    NUMBER(5),
    CREATED_AT      DATE DEFAULT SYSDATE,
    CONSTRAINT PK_STEP3_SUMMARY PRIMARY KEY (PLYNO)
);