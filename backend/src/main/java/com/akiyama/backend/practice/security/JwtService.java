package com.akiyama.backend.practice.security;


import java.util.Date;
import java.util.function.Function;

// JWT署名用の秘密鍵型（HS256など）
import javax.crypto.SecretKey;

// application.properties の値を読み込む
import org.springframework.beans.factory.annotation.Value;
// Spring Securityのユーザー情報（ログインユーザー）
import org.springframework.security.core.userdetails.UserDetails;
// Spring Beanとして登録
import org.springframework.stereotype.Service;

// JWTの中身（payload）
import io.jsonwebtoken.Claims;
// JWTの生成・解析のメインクラス
import io.jsonwebtoken.Jwts;
// Base64デコード用
import io.jsonwebtoken.io.Decoders;
// SecretKey生成用ユーティリティ
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    // application.propertiesの秘密鍵を取得
    @Value("${jwt.secret}")
    private String secretKey;
    
    // application.propertiesのJWTの有効期限（ミリ秒）を取得
    @Value("${jwt.expiration}")
    private long expirationTime ;
    
    // JWTからユーザー名（sub）を取得
    public String extractUsername(String token) {
        // subject（= username）を取り出す
        return extractClaim(token, claims -> claims.getSubject());
    }

    // 任意のClaim取得
    // 下記はJWTの中身を自由に取り出す汎用メソッ
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {
        // JWTを完全解析して中身取得
        final Claims claims = extractAllClaims(token);
        // 取り出しルールを適用（subjectとかroleとか）
        return claimsResolver.apply(claims);  
    }

    // JWT全情報取得
    private Claims extractAllClaims(String token) {
        // JWTパーサー作成
        return Jwts.parser() 
                .verifyWith(getSigningKey()) // 署名検証（改ざんチェック）
                .build() // パーサー確定
                .parseSignedClaims(token) // JWTを解析（署名付きトークン）
                .getPayload(); // 中身（claims）だけ取得
    }

    // ログイン成功時にJWTを作る
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        // 有効期限設定
        Date expirationDate = new Date(now.getTime() + expirationTime );
        // JWT作成開始
        return Jwts.builder()
                .subject(userDetails.getUsername()) // usernameをJWTに埋め込む
                .issuedAt(now) // 発行時間
                .expiration(expirationDate) // 期限設定
                .signWith(getSigningKey()) // 署名（改ざん防止）
                .compact(); // JWT文字列完成
    }

    // トークン検証（JWTが正しいかチェック）
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // JWTからユーザー名取得
        String username = extractUsername(token);
        // DBユーザーと一致するか
        return username.equals(userDetails.getUsername())
                // 期限切れでないか
                && !isTokenExpired(token);
    }

    // 期限チェック
    private boolean isTokenExpired(String token) {
        // 現在時刻より前なら期限切れ
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
    
    // JWT署名に使う鍵を作る
    public SecretKey getSigningKey() {
        // Base64デコード前提（実務推奨）
        byte[] KeyBytes = Decoders.BASE64.decode(secretKey);
        // HMAC用SecretKey生成
        return Keys.hmacShaKeyFor(KeyBytes);
    }
}
