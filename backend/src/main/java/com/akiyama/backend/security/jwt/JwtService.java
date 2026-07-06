package com.akiyama.backend.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 役割：
// JWTの生成・署名検証・有効期限確認・Claim取得を行う
public class JwtService {
    // JWT設定
    private final JwtProperties jwtProperties;

    /**
     * JWT生成
     */
    // Spring Securityの UserDetails（ログイン済みユーザー）を受け取る
    public String generateToken(UserDetails userDetails) {
        // トークン発行時刻（issuedAt）になる
        Date now = new Date();
        // 有効期限の計算を現在時刻 + 有効時間（ミリ秒）で作成
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration());
        // JWTを組み立てるビルダー開始
        return Jwts.builder()
                // subject（誰のトークンか）：トークンの持ち主を設定　実質ログインID
                .subject(userDetails.getUsername())
                // 発行時刻
                .issuedAt(now)
                // 有効期限
                .expiration(expiration)
                // 署名：改ざん防止のためのデジタル署名
                // サーバーだけが知っている秘密鍵で署名
                // 改ざんされたら検知できる
                .signWith(getSigningKey())
                // JWT文字列として完成 例：eyJhbGciOiJIUzI1NiJ9...
                .compact();
    }

    /**
     * JWTからユーザー名取得
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 任意のClaim取得
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Claims取得
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * JWT有効期限確認
     */
    private boolean isTokenExpired(String token) {

        Date expiration = extractClaim(token, Claims::getExpiration);

        return expiration.before(new Date());
    }

    /**
     * トークン検証
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * 署名鍵取得
     */
    // 「JWTに署名するための“秘密鍵（SecretKey）”を作っている処理」
    private SecretKey getSigningKey() {
        // Keys.hmacShaKeyFor(byte[])：JWT署名用のHMAC鍵を生成するメソッド
        return Keys.hmacShaKeyFor(
                // 秘密文字列
                jwtProperties.getSecret()
                       // JWTライブラリは「文字列」ではなく「バイト列」を使うため変換
                       .getBytes(StandardCharsets.UTF_8)); 
    }

}