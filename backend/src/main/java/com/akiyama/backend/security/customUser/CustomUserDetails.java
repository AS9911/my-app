package com.akiyama.backend.security.customUser;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.akiyama.backend.domain.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// 役割：
// UserエンティティをSpring SecurityのUserDetailsへ変換する
public class CustomUserDetails implements UserDetails {

    private final User user;

    // 権限情報
    @Override
    // Spring Securityに対して「このユーザーはどんな権限を持っているか？」を返すメソッド
    // ? extends GrantedAuthority：「GrantedAuthorityまたはその子クラスならOK」
    // ->Collection<何でもいいけど GrantedAuthority系のもの>
    // ジェネリクス型を使ったのは実装の柔軟性を高めるため
    // ★GrantedAuthority は「権限（authority）」を表すためのインターフェースであり、下記が実装クラス★
    // １．new SimpleGrantedAuthority("ROLE_USER")：最も標準的な実装、ただの「文字列ベース権限」を保持するだけ、immutable（変更不可）
    // ２．SwitchUserGrantedAuthority:「代理ログイン（Switch User）」専用
    // ３．カスタムGrantedAuthority　GrantedAuthorityを実装したクラスで独自の権限を付与する場合
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ロールがないユーザーは「権限なし」
        // 空リストを返すことでアクセス不可になる
        if (user.getRole() == null) {
            return Collections.emptyList();
        }
        // 権限の作成
        // Collections.singletonList()：「要素が1つだけ入った変更不可（immutable）のListを作るメソッド」
        // ★複数の権限がある場合はList.ofを使うなども出来る★
        // new SimpleGrantedAuthority("ROLE_USER")：Spring Securityが理解できる「権限オブジェクト」
        // ->この部分がGrantedAuthority の実装クラス
        // ★権限オブジェクトはAuthenticationオブジェクトの中に格納されてSecurityContextに保持される★
        // JWT認証の場合、毎リクエスト毎にJWTから復元されるため、サーバーでは基本保持しない
        // Authentication:ログインしたユーザーの認証情報そのもの
        // SecurityContext:Authenticationを保持する入れ物
        // SecurityContextHolder:SecurityContextを保存する場所
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // パスワード
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ユーザー名
    @Override
    public String getUsername() {
        return user.getUserId();
    }
    
    // アカウント有効期限
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // ロック状態
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // パスワード有効期限
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 利用可能か
    @Override
    public boolean isEnabled() {
        return true;
    }
}
