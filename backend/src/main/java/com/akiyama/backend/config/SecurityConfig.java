package com.akiyama.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import com.akiyama.backend.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

// このクラスは設定クラスと知らせるアノテーション
// SpringBoot起動時に読み込まれ、Bean定義を登録するためのクラス
// ->起動->@Configurationを探す->@Beanを登録->Springコンテナ完成
// DB設定、Jackson設定、CORS設定、Cache設定などでも利用する
@Configuration
// SpringSecurityを有効するアノテーション
// Security Filter Chainを登録
// SpringSecurityのFilterを組み立て
// AuthenticationManagerを構築
// HttpSecurityを有効化
// 通常はリクエスト->DispatcherServletだがこの間にSecurity Filter Chainを置くことが出来る
// ※DispatcherServlet：Spring MVCおよびSpring BootのWebアプリケーションにおけるフロントコントローラ
// リクエストを受け取り、適切なコントローラーメソッドへルーティングし、その実行からレスポンス生成までを統括するフロントコントローラ
@EnableWebSecurity
// メソッド単位の認可を有効にする。
// 下記が利用可能となる
// @PreAuthorize：@PreAuthorize("hasRole('ADMIN')")　ADMINだけ実行可能なメソッドに出来る
// @PostAuthorize：メソッド実行後に認可を行う->要は戻り値で認可を行う
// @Secured：シンプルにロール認可を行う
// @RolesAllowed：Springに非依存のロール認可
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) 
            throws Exception {
        
        // ここに各種セキュリティの設定を追加していく。
        // ★「HTTPリクエストに対してどのようなセキュリティ処理を適用するか」を設定する場所★
        http    
                // CSRF無効（REST　APIでは基本OFF設定）
                // CSRF：セッションベース認証を前提とした保護機能のため、
                // JWTのみを使うAPIでは通常無効化する。
                .csrf(csrf -> csrf.disable())

                // CORS有効化（Reactから通信が受け取れない）
                // Customizer.withDefaults()で良い理由
                // CorsConfigで「public CorsConfigurationSource corsConfigurationSource()」をBeanとして登録した
                // cors()が動作する際、内部的にSpringContainerにCorsConfigurationSource型のBeanにあるかを探しに行く
                // ある場合、Customizer.withDefaults() は「デフォルトのカスタマイズ方法でそれを利用してください」という指示を与えている
                .cors(Customizer.withDefaults())

                // Security Headers：
                // HTTPレスポンスに付与されるもの
                // このヘッダを読み、JavaScriptをどこから読み込めるか
                // iframeで表示良いか、Httpsを強制するか、カメラを利用させるかを判断
                // ★http.headers(...)：HTTPレスポンスヘッダを設定（この中で複数のセキュリティヘッダを追加）
                .headers(headers -> headers
                        // ★Content Security Policy（CSP）：
                        // ※CSPとはブラウザに対する「読み込みルール」
                        // Content-Security-Policyヘッダになる
                        .contentSecurityPolicy(csp -> csp
                                // default-src 'self'：全てのリソースは同じサーバのみ
                                // script-src 'self'；JavaScriptは自サイトだけ許可（外部から悪意あるJavaScriptを読み込むことを防止）
                                // style-src 'self' ：'unsafe-inline'：'unsafe-inline' はXSSのリスクを高める可能性があるため、可能であれば避けるのが望ましい設定
                                // img-src 'self' data:　：画像の読み込み元
                                // object-src 'none'：Flash、Java Applet、Silverlightを完全禁止　※noneが一般的
                                // frame-ancestors 'none'；サイトに<iframe>へ埋め込めません。->クリックジャッキング対策
                                .policyDirectives(
                                        "default-src 'self'; " +
                                        "script-src 'self'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "img-src 'self' data:; " +
                                        "object-src 'none'; " +
                                        "frame-ancestors 'none'"
                                )
                        )
                        // HTTPヘッダ「Referrer-Policy」になる
                        // Referrer；
                        .referrerPolicy(referrer -> 
                                referrer.policy(
                                        ReferrerPolicyHeaderWriter
                                                .ReferrerPolicy
                                                .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                )
                        )
                        // HTTPヘッダ「Permissions-Policy」
                        // 役割：ブラウザAPI禁止とする
                        .addHeaderWriter(
                                // camera=()：カメラ禁止
                                // microphone=()：マイク禁止
                                // geolocation=()：位置情報禁止->navigator.geolocationを利用出来なくなる(Reactでも使えない)
                                new StaticHeadersWriter(
                                        "Permission-policy",
                                        "camera=(), microphone=(), geolocation"
                                )
                        )
                        // HTTPヘッダ「Strict-Transport-Security」
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(false)
                                .maxAgeInSeconds(31536000)
                        )
                )

                // セッション：サーバー側でログイン状態を保持するということ
                // @PostMapping("/login")でログインすると
                // SpringSecurityは自動で「JSESSIONID」というCookieを発行している
                // 今回はJWT認証を行うためにセッション認証を無効化する
                // ⇒RestfulなAPIを作るためにステートレスとする必要がある
                // .sessionManagement(...)；Sessionの管理方法を設定
                .sessionManagement(session -> 
                        // session.sessionCreationPolicy(...)：Sessionをいつ作る？を決定する
                        // SessionCreationPolicy.STATELESS：絶対にSessionを作らない
                        // ★SessionCreationPolicyの種類★
                        // ALWAYS     ：毎回sessionを生成する（ほとんど使わない）
                        // IF_REQUIRED：sessionを必要になれば作る（Spring Securityのデフォルト）
                        // NEVER      ：sessionを自分で作らない（ただし、既にsessionがあれば利用する）
                        // STATELESS  ：Session禁止（JWT認証ならこれが一択）
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                )
                
                // 認可ルール
                .authorizeHttpRequests(auth -> auth
                        // プリフライト
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ログイン
                        .requestMatchers("/api/auth/login").permitAll()
                        // 新規登録
                        .requestMatchers("/api/auth/register").permitAll()
                        // 管理者のみ
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // USER, ADMIN
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                        // その他
                        .anyRequest().authenticated() 
                )

                // JWTフィルタ追加
                .addFilterBefore(
                        jwtAuthenticationFilter, 
                        UsernamePasswordAuthenticationFilter.class
                );
        
        // デフォルトではこれだけでレスポンス時に下記のSecurity Headersを付与する
        // X-Content-Type-Options: nosniff　：Content-Type以外は信用しないという動作になる
        // X-Frame-Options: DENY：クリックジャッキング対策
        // Cache-Control：ブラウザやプロキシのキャッシュ方法を制御
        // Pragma：HTTP/1.0　向けのキャッシュ制御
        // Expires キャッシュの有効期限を制御
        return http.build();
    }
}
