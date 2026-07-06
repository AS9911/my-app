package com.akiyama.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Configuration;

// ★Securityに追加する必要はない
// ★登録時の処理やログイン処理のサービス層で利用する
// 利用法は通常のコンストラクタインジェクションで良い
// String encodedPassword = passwordEncoder.encode(password);
// passwordEncoder.matches(request.getPassword(), user.getPassword())
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }   
}
