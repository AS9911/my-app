package com.akiyama.backend.practice.infra.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.akiyama.backend.practice.domain.entity.User;

@Mapper
public interface UserRepository {

    Optional<User> findByUsername(String username);

    void insertUser(User user);
    
}
