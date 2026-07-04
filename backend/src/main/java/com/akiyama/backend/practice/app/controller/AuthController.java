package com.akiyama.backend.practice.app.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akiyama.backend.config.LoginUser;
import com.akiyama.backend.practice.app.dto.request.LoginRequest;
import com.akiyama.backend.practice.app.dto.response.LoginResponse;
import com.akiyama.backend.practice.app.dto.token.RefreshRequest;
import com.akiyama.backend.practice.domain.service.CustomUserDetailsService;
import com.akiyama.backend.practice.security.JwtService;
import com.akiyama.backend.practice.security.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    // ログイン
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {
        
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(), 
                                request.getPassword()
                        )
                );

        LoginUser user = (LoginUser) auth.getPrincipal();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generatedRefreshToken(user);
        LoginResponse response = new LoginResponse(accessToken, refreshToken);

        return response;
    }

    // トークン再発行
    @PostMapping("/refresh")
    public LoginResponse login (
            @RequestBody  RefreshRequest request) {
    
        String refreshToken = request.getRefreshToken();

        String username = jwtService.extractUsername(refreshToken);

        UserDetails user = userDetailsService.loadUserByUsername(username);
        
        if (!refreshTokenService.isValid(refreshToken, user)) {
            throw new RuntimeException("invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        
        LoginResponse response = new LoginResponse(newAccessToken, refreshToken);
        
        return response;
    }
}
