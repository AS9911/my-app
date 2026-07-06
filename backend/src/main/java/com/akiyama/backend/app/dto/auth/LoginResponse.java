package com.akiyama.backend.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// 役割：
// JWTを返却するレスポンスDTO
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }
}
