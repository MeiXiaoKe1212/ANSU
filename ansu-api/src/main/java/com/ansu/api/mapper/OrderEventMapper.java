package com.ansu.api.mapper;

import com.ansu.api.domain.entity.OrderEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单事件Mapper接口
 */
@Mapper
public interface OrderEventMapper extends BaseMapper<OrderEvent> {
}
