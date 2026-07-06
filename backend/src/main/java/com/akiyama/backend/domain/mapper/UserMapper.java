package com.akiyama.backend.domain.mapper;

import com.akiyama.backend.domain.entity.User;

// @Mapper
public interface UserMapper {
    // 引数にはリクエストのDTOで渡すのが良い
    public User findByUserId(String userId);
} 
