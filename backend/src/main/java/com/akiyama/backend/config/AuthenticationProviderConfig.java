package com.akiyama.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.akiyama.backend.security.customUser.CustomUserDetailsService;

@Configuration
// 役割:
// DaoAuthenticationProvider を生成し、
// CustomUserDetailsService と
// PasswordEncoder を関連付ける     
public class AuthenticationProviderConfig {

    /**
     * 認証プロバイダー
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}
