package com.akiyama.backend.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akiyama.backend.app.dto.auth.LoginRequest;
import com.akiyama.backend.app.dto.auth.LoginResponse;
import com.akiyama.backend.domain.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// 役割：
// ログインAPI（/api/auth/login）を提供する
public class AuthController {

    private final AuthService authService;

    /**
     * ログイン
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Validated @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            // 返却したJWTはフロント側でローカルストレージやクッキーに保存する
            // LocalStorageの場合：自動送信 ❌（されない）のでJavaScriptで毎回付与する必要あり
            // ★const token = localStorage.getItem("token");
            //     fetch("/api/user", {
            //     headers: {
            //         Authorization: `Bearer ${token}`
            //     }
            // });
            // 今回の検証はAuthorizationヘッダーを読む一般的な手法で行っている
            // ★Cookieの場合：同一ドメインなら自動付与
            // 「同一ドメインかどうか」はブラウザが内部的にURLベースで厳密に判定している
            // ->ブラウザは「URLのドメイン」と「Cookieに設定されたDomain属性」を照合して、自動送信するか判断
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
