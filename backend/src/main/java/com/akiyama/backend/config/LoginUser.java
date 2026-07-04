package com.akiyama.backend.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.akiyama.backend.practice.domain.entity.User;

import lombok.RequiredArgsConstructor;

// SpringSecurityがユーザーを認識出来る
// ROLE認可（haRole）が動く
// SecurityContextに入る情報が完成する
// ※SecurityContext：「現在ログインしているユーザーの認証情報を保持する箱」
@RequiredArgsConstructor
// Spring Securityが扱う「ユーザー形式」に変換
public class LoginUser implements UserDetails {
    // DBから取得した実体
    private final User user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                // DB値 Spring内部
                // ADMIN ROLE_ADMIN
                // USER ROLE_USER
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole()
                )
        );
    }
    
    // パスワード
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ユーザー名（ログインID）
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    // アカウント有効期限（今回は常にtrue）
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // アカウントロック状態
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // パスワード期限
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 有効ユーザーかどうか
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 元のUser取得（必要な場合用）
    public User getUser() {
        return user;
    }
}
