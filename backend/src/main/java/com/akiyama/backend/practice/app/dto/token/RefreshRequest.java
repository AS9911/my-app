package com.akiyama.backend.practice.app.dto.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
    
    private String refreshToken;
}
