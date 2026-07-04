package com.akiyama.backend.config;

import com.akiyama.backend.practice.security.CustomAccessDeniedHandler;
import com.akiyama.backend.practice.security.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

// Springの設定クラスとして登録する。
@Configuration
// SpringSecurityを有効化する。
// Serviceなどで「@PreAuthorize("hasRole('ADMIN')")」を使用可能
@EnableWebSecurity
// Lombokによって、final フィールドを引数に持つコンストラクタが自動生成
// コンストラクタ：オブジェクト（インスタンス）が生成されるときに自動的に呼び出される特別なメソッド
@RequiredArgsConstructor
public class SecurityConfig {

    // ★「画面（Webアプリケーション）」向けの設定
    // APIでも一部は共通ですが、そのまま使うことはない
    
    // // Spring管理のBeanとして登録する。
    // @Bean
    // // 認証・認可の設定を行うメソッド
    // SecurityFilterChain securityFilterChain(
    //         HttpSecurity http)
    //         throws Exception {
    //     // http=>Spring Securityの設定開始。
    //     // .authorizeHttpRequests(...)：URLごとのアクセス権限を設定
    //     http.authorizeHttpRequests(auth -> auth
    //                     // ログイン画面は誰でもアクセス可能。
    //                     // ★requestMatchers：
    //                     // Spring Securityで
    //                     // 「どのURLにどの認可ルールを適用するか」を指定するメソッド
    //                     // 要は、「このURLにアクセスしたときは、
    //                     // このルールを適用してください」とSpring Securityに教えるメソッド
    //                     // ★permitAll()；
    //                     // Spring Securityで
    //                     // 「認証（ログイン）していなくてもアクセスを許可する」メソッドです
    //                     .requestMatchers("/login").permitAll()
    //                     // 管理者のみアクセス可能
    //                     // ★hasRole()：
    //                     // Spring Securityで
    //                     // 「指定したロール（権限）を持つユーザーだけアクセスを許可する」メソッド
    //                     // /**と記載することで/admin配下の全てのURLが対象となる
    //                     // *では1階層下のみが対象となるが、**では何階層でも対象と出来る
    //                     .requestMatchers("/admin/**").hasRole("ADMIN")
    //                     // その他はログイン必須
    //                     // ★.anyRequest()：
    //                     // Spring Securityで
    //                     // 「これまでに指定していないすべてのリクエスト」を対象にするメソッド
    //                     // 「上で設定していない残りのURL全部」という意味のメソッド
    //                     // ★authenticated() ：
    //                     // Spring Securityで
    //                     // 「認証（ログイン）済みのユーザーだけアクセスを許可する」
    //                     .anyRequest().authenticated())
    //             // フォームログインを有効
    //             // ★formLogin() ：
    //             // Spring Securityでフォーム認証（ログイン画面を使った認証）の設定を行うメソッド
    //             // ★loginPage() ：
    //             // Spring Securityで「どのURLをログイン画面として使用するか」を指定するメソッド
    //             .formLogin(form -> form
    //                     // 独自ログイン画面
    //                     .loginPage("/login")
    //                     // ログイン成功後の遷移先
    //                     .defaultSuccessUrl("/")
    //                     .permitAll())
    //             // ★logout：
    //             // 「ログアウト機能をどのように動作させるか」★
    //             // ★logoutUrl：
    //             // ログアウト処理を実行するURLを指定するメソッド
    //             .logout(logout -> logout
    //                     // ログアウトURL
    //                     .logoutUrl("/logout")
    //                     // ログアウト後の画面
    //                     .logoutSuccessUrl("/login"));
    //     // http.build() は、
    //     // これまで HttpSecurity に設定してきた内容をもとに、
    //     // 実際に動作する SecurityFilterChain オブジェクトを生成するメソッド
    //     return http.build();
    // }

    // ★API版はこっち★

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    // 自作するJWT認証フィルター
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    
    @Bean
    // Spring Securityの設定を組み立てるメソッド
    // ここで作成されたSecurityFilterChainが、起動時にTomcatへ登録
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {
        
        http
                // CSRF保護を無効化→JWTをAuthorizationヘッダーで送るAPIでは一般的な設定
                // 保護を無効化をするのはJWT認証ではCSRF攻撃が成立しにくいため
                // CSRF（Cross-Site Request Forgery：ログイン中のユーザーになりすまして、悪意のあるサイトから勝手にリクエストを送信させる攻撃
                // ブラウザがCookieを自動で送信するため サイトが本人と勘違いして悪意あるサイトからのリクエストを受け入れてしまう。
                .csrf(csrf -> csrf.disable())

                // セッションを作成しない設定
                // JWT認証ではログイン状態をサーバーに保持しないため、この設定が重要
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // CORS設定を有効化（Reactなど別オリジンからアクセスする場合に利用）
                .cors(Customizer.withDefaults())

                // URLごとの認可設定を開始
                .authorizeHttpRequests(auth -> auth
                        // ログインAPIは認証前なので誰でもアクセス許可
                        .requestMatchers("/api/login").permitAll()
                        // 公開APIも認証不要
                        .requestMatchers("/api/public/**").permitAll()
                        // 管理者ロールを持つユーザーだけがアクセス許可
                        .requestMatchers("api/admin/**").hasRole("ADMIN")
                        // それ以外のAPIはログイン済み（JWT認証済み）であることを要求
                        .anyRequest().authenticated())
                
                // 例外処理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                
                // JWTフィルタ
                // Spring SecurityのFilter Chainに、自作のJwtAuthenticationFilterを追加
                // 「UsernamePasswordAuthenticationFilterより前で実行してください」という意味
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
                
        // ここまで設定した内容をもとにSecurityFilterChainを生成して返却
        // Spring Boot起動時に、このSecurityFilterChainがBeanとして登録
        return http.build();
    } 


    @Bean
    // パスワードを暗号化・比較するクラス
    // 認証時は、入力された平文パスワードとDBのBCryptハッシュを比較
    PasswordEncoder passwordEncoder() {
        // BCrypt方式でハッシュ化
        return new BCryptPasswordEncoder();
    }

    @Bean
    // ログインAPIで「authenticationManager.authenticate(...)」を呼び出すためのBean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}
