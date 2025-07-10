package com.ansu.api.mapper;

import com.ansu.api.domain.entity.Driver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 司机Mapper接口
 */
@Mapper
public interface DriverMapper extends BaseMapper<Driver> {
}
