package com.akiyama.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
// 役割：
// AuthenticationManager と
// PasswordEncoder をBeanとして登録する
public class AuthenticationManagerConfig {

    /**
     * AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}
