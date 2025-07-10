package com.ansu.api.mapper;

import com.ansu.api.domain.entity.OrderCost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单成本Mapper接口
 */
@Mapper
public interface OrderCostMapper extends BaseMapper<OrderCost> {
}
