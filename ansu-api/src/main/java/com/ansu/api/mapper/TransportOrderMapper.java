package com.ansu.api.mapper;

import com.ansu.api.domain.entity.TransportOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运输订单Mapper接口
 */
@Mapper
public interface TransportOrderMapper extends BaseMapper<TransportOrder> {
}
