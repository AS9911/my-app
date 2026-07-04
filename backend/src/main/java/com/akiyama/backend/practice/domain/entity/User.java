package com.akiyama.backend.practice.domain.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ログインID
    @Column(nullable = false, unique = true)
    private String username;

    // BCrypt暗号化パスワード
    @Column(nullable = false)
    private String password;

    // 権限（ROLE_USER / ROLE_ADMINなど）
    @Column(nullable = false)
    private String role;

    // アカウント有効フラグ
    @Column(nullable = false)
    private boolean enabled = true;
}
