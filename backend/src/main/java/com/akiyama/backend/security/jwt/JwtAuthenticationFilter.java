package com.akiyama.backend.security.jwt;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.akiyama.backend.security.customUser.CustomUserDetailsService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// 役割：
// リクエストのJWTを検証し、認証情報をSecurityContextへ設定する
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = getToken(request);

        if (token != null) {

            String username = jwtService.extractUsername(token);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * AuthorizationヘッダーからJWTを取得
     */
    private String getToken(HttpServletRequest request) {

        String header =
                request.getHeader("Authorization");

        if (StringUtils.hasText(header)
                && header.startsWith("Bearer ")) {

            return header.substring(7);
        }

        return null;
    }

}
