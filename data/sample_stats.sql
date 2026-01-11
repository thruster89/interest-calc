-- 계약 수
SELECT COUNT(*) FROM CONTRACT;

-- 입금 총 건수
SELECT COUNT(*) FROM DEPOSIT;

-- 계약당 입금 분포
SELECT
    MIN(cnt) AS min_cnt,
    MAX(cnt) AS max_cnt,
    ROUND(AVG(cnt), 1) AS avg_cnt
FROM (
    SELECT PLYNO, COUNT(*) cnt
    FROM DEPOSIT
    GROUP BY PLYNO
);