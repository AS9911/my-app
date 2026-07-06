package com.akiyama.backend.security.customUser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.akiyama.backend.domain.entity.User;
import com.akiyama.backend.domain.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 役割：
// ユーザーIDをもとにDBからユーザーを取得し、CustomUserDetailsを返す
// UserDetailsService = 「ユーザー名からユーザー情報をDBなどから取得するサービス」
// SpringSecurityを標準的に実装するならUserDetailsServiceの実装クラスするのが正解
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String userId)
            throws UsernameNotFoundException {

        User user = userMapper.findByUserId(userId);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "ユーザーが存在しません。");
        }

        return new CustomUserDetails(user);      
    }
}
