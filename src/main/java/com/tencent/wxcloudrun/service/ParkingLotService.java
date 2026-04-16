package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.ParkingLotQueryDTO;
import com.tencent.wxcloudrun.model.ParkingLot;

import java.util.List;

public interface ParkingLotService {
    ParkingLot getById(Long id);
    List<ParkingLot> getList(ParkingLotQueryDTO query);
    List<ParkingLot> getHotList(String city, int limit);
}
