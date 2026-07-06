package com.akiyama.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String userId;
    private String username;
    private String password;
    private String role;
}
