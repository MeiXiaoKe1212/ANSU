package com.ansu.api.mapper;

import com.ansu.api.domain.entity.Tenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户数据访问层
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
