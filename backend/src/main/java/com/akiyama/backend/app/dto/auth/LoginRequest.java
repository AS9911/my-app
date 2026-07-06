package com.akiyama.backend.app.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 役割：
// ログインAPIのリクエストDTO
public class LoginRequest {
    
    @NotNull(message = "{userId.required}")
    private String userId;
    
    @NotNull(message = "{password.required}")
    private String password;
}
