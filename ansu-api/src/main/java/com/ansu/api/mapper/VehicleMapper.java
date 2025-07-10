package com.ansu.api.mapper;

import com.ansu.api.domain.entity.Vehicle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车辆Mapper接口
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
}
