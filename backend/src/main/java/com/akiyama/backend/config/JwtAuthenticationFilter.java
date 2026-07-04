package com.akiyama.backend.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.akiyama.backend.practice.domain.service.CustomUserDetailsService;
import com.akiyama.backend.practice.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// SpringBean（Springコンテナ（IoCコンテナ）が生成・管理しているオブジェクト）としてDIコンテナに登録
@Component
@RequiredArgsConstructor
// このクラスはJWT認証の中核であり、すべてのAPIリクエストで自動的に実行されるFilter
// Controllerクラス前に実行される
public class JwtAuthenticationFilter
        // Spring SecurityのFilter=>1リクエストにつき1回だけ実行
        extends OncePerRequestFilter{
    
    // JWT解析、検証、作成するクラス
    private final JwtService jwtService;
    // DBからユーザー情報を取得
    private final CustomUserDetailsService userDetailsService;
    
    @Override
    // このメソッドがリクエスト毎に自動実行される
    // protected：同じパッケージ内、または継承したクラスからアクセスを許可する
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        
        // HTTPヘッダー「Authorization: Bearer xxxxxxxxx」を取得
        final String authorizationHeader = 
                request.getHeader("Authorization");
        
        // JWTの有無を確認
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            // JWTがない場合、次のFilterへ処理を渡す
            filterChain.doFilter(request, response);
            return;
        }
        // Bearerを除去して、トークン部分を取得
        String jwt = authorizationHeader.substring(7);
        // JWTからsubを取得する（ユーザー名を取得）
        String username = jwtService.extractUsername(jwt);

        if (username != null
                // まだ認証されていないことを確認（二重認証防止のため）
                && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            // DBからユーザー情報を取得。
            UserDetails userDetails = 
                    userDetailsService.loadUserByUsername(username);

            // JWT検証（署名、有効期限、ユーザーなどを確認）
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Spring Securityが扱う認証済みユーザーを作成
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                            // 中身はユーザー、資格情報、権限
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                
                // IPアドレス、Session情報などを保持
                authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

                // SecurityContext登録
                // これによりauthentication.getName()
                // @PreAuthorize(...)などが利用可能になる
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        // 認証が終わったので次の処理に進める
        filterChain.doFilter(request, response);
    }
}
