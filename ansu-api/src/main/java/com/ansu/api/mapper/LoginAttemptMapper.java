package com.ansu.api.mapper;

import com.ansu.api.domain.entity.LoginAttempt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录尝试记录数据访问层
 */
@Mapper
public interface LoginAttemptMapper extends BaseMapper<LoginAttempt> {
}
