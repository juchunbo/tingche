package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.ParkingLotMapper;
import com.tencent.wxcloudrun.dto.ParkingLotQueryDTO;
import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.service.ParkingLotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    
    @Autowired
    private ParkingLotMapper parkingLotMapper;
    
    @Override
    public ParkingLot getById(Long id) {
        return parkingLotMapper.selectById(id);
    }
    
    @Override
    public List<ParkingLot> getList(ParkingLotQueryDTO query) {
        return parkingLotMapper.selectList(query);
    }
    
    @Override
    public List<ParkingLot> getHotList(int limit) {
        return parkingLotMapper.selectHotList(limit);
    }
}
