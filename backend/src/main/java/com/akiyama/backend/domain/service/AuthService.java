package com.akiyama.backend.domain.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.akiyama.backend.app.dto.auth.LoginRequest;
import com.akiyama.backend.app.dto.auth.LoginResponse;
import com.akiyama.backend.security.customUser.CustomUserDetails;
import com.akiyama.backend.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 役割：
// 認証処理を実行し、JWTを発行する業務ロジックを担当する
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * ログイン
     */
    public LoginResponse login(LoginRequest request) {
        // 認証成功後の結果が入る
        // 中身：
        // principal   = UserDetails（ユーザー情報）
        // credentials = null（通常消える）
        // authorities = ROLE_USER など
        Authentication authentication =
                // authenticationManager.authenticate()
                // ->「このユーザーIDとパスワード正しいか？」をSpringに判定させている
                // authenticate()を呼んだ時点で、内部的にはDBアクセスまで動く
                // authenticationManager.authenticate()
                // ↓
                // AuthenticationProvider
                // ↓
                // UserDetailsService.loadUserByUsername()
                //   ※実装クラスが動く（今回はCustomUserDetailsService）
                //     より正確に言えば@ServiceをつけてDI登録されたものが呼ばれる
                //     SpringSecurityはインターフェースを呼ぶだけで
                //     実際の処理はDIされた実装クラスが動作する
                // ↓
                // （ここでDBアクセス）
                // ↓
                // UserDetails取得
                // ↓
                // PasswordEncoderで比較
                // ↓
                // 認証成功/失敗
                authenticationManager.authenticate(
                        // 下記は未認証状態のトークン
                        new UsernamePasswordAuthenticationToken(
                                request.getUserId(),
                                request.getPassword()));
        // ここでprincipalを取り出している
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        // 「このユーザーはログイン済み」と証明するトークンを作る
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }
    
}
