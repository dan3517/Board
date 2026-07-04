package com.example.board.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final String BEARER_PREFIX =
            "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (token != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                authenticate(
                        request,
                        token
                );
            }

            filterChain.doFilter(
                    request,
                    response
            );

        } catch (
                JwtAuthenticationException |
                UsernameNotFoundException exception
        ) {
            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    exception
            );
        }
    }

    private void authenticate(
            HttpServletRequest request,
            String token
    ) {
        Long memberId =
                jwtTokenProvider.extractAccessTokenMemberId(token);

        CustomUserDetails userDetails =
                userDetailsService.loadUserByMemberId(
                        memberId
                );

        Authentication authentication =
                UsernamePasswordAuthenticationToken
                        .authenticated(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

        ((UsernamePasswordAuthenticationToken) authentication)
                .setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private String resolveToken(
            HttpServletRequest request
    ) {
        String authorizationHeader =
                request.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new JwtAuthenticationException(
                    com.example.board.global.exception
                            .ErrorCode.INVALID_TOKEN
            );
        }

        String token = authorizationHeader
                .substring(BEARER_PREFIX.length())
                .strip();

        if (!StringUtils.hasText(token)) {
            throw new JwtAuthenticationException(
                    com.example.board.global.exception
                            .ErrorCode.INVALID_TOKEN
            );
        }

        return token;
    }
}