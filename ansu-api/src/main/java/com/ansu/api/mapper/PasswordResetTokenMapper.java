package com.ansu.api.mapper;

import com.ansu.api.domain.entity.PasswordResetToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密码重置令牌数据访问层
 */
@Mapper
public interface PasswordResetTokenMapper extends BaseMapper<PasswordResetToken> {
}
