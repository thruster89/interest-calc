package com.interestcalc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interestcalc.domain.Deposit;

@Mapper
public interface DepositMapper {

    List<Deposit> selectDeposits(
            @Param("runMode") String runMode,
            @Param("targetPlyNo") String targetPlyNo);
}
