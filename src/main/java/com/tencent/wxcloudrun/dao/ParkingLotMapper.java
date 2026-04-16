package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.dto.ParkingLotQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingLotMapper {
    
    ParkingLot selectById(@Param("id") Long id);
    
    List<ParkingLot> selectList(ParkingLotQueryDTO query);
    
    List<ParkingLot> selectHotList(@Param("city") String city, @Param("limit") int limit);
    
    int insert(ParkingLot parkingLot);
    
    int updateById(ParkingLot parkingLot);
}
