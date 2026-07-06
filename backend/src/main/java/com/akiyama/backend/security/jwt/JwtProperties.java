package com.akiyama.backend.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

// コンストラクタインジェクションで利用できるようになる
@Component
// jwt プレフィックスを持つ設定値を、このクラスへマッピング
// application.properties の「jwt.secret=...とjwt.expiration=...をフィールドにマッピングする」
// 役割：
// application.properties のJWT設定（秘密鍵・有効期限）を保持する
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    // JWT認証に利用する秘密鍵
    private String secret;
    // アクセストークンの有効期限
    private long expiration;
    
}
