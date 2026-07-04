package com.example.board.domain.member.service;

import com.example.board.domain.member.dto.request.LoginRequest;
import com.example.board.domain.member.dto.request.ReissueRequest;
import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.dto.response.TokenResponse;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.RefreshToken;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.member.repository.RefreshTokenRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.global.security.JwtTokenProvider;
import com.example.board.global.security.RefreshTokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenHasher refreshTokenHasher;

    public SignupResponse signup(
            SignupRequest request
    ) {
        String email =
                normalizeEmail(request.email());

        String nickname =
                normalizeNickname(request.nickname());

        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);

        String encodedPassword =
                passwordEncoder.encode(
                        request.password()
                );

        Member member = Member.create(
                email,
                encodedPassword,
                nickname
        );

        Member savedMember =
                memberRepository.save(member);

        return SignupResponse.from(savedMember);
    }

    public TokenResponse login(
            LoginRequest request
    ) {
        String email =
                normalizeEmail(request.email());

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken
                                    .unauthenticated(
                                            email,
                                            request.password()
                                    )
                    );

            CustomUserDetails principal =
                    (CustomUserDetails)
                            authentication.getPrincipal();

            JwtTokenProvider.TokenPair tokenPair =
                    jwtTokenProvider.issueTokenPair(
                            principal.getMemberId()
                    );

            saveOrRotateRefreshToken(
                    principal.getMemberId(),
                    tokenPair.refreshToken()
            );

            return TokenResponse.from(tokenPair);

        } catch (AuthenticationException exception) {
            throw new BusinessException(
                    ErrorCode.INVALID_LOGIN
            );
        }
    }

    public TokenResponse reissue(
            ReissueRequest request
    ) {
        String rawRefreshToken =
                request.refreshToken();

        Long memberId =
                jwtTokenProvider
                        .extractRefreshTokenMemberId(
                                rawRefreshToken
                        );

        RefreshToken storedRefreshToken =
                refreshTokenRepository
                        .findByMemberIdForUpdate(memberId)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.INVALID_REFRESH_TOKEN
                                )
                        );

        validateStoredRefreshToken(
                storedRefreshToken,
                rawRefreshToken
        );

        if (!storedRefreshToken
                .getMember()
                .isActive()) {

            refreshTokenRepository.delete(
                    storedRefreshToken
            );

            throw new BusinessException(
                    ErrorCode.INVALID_REFRESH_TOKEN
            );
        }

        JwtTokenProvider.TokenPair newTokenPair =
                jwtTokenProvider.issueTokenPair(
                        memberId
                );

        String newRefreshTokenHash =
                refreshTokenHasher.hash(
                        newTokenPair
                                .refreshToken()
                                .value()
                );

        storedRefreshToken.rotate(
                newRefreshTokenHash,
                newTokenPair
                        .refreshToken()
                        .expiresAt()
        );

        return TokenResponse.from(
                newTokenPair
        );
    }

    public void logout(Long memberId) {
        refreshTokenRepository
                .deleteByMemberId(memberId);
    }

    private void saveOrRotateRefreshToken(
            Long memberId,
            JwtTokenProvider.IssuedToken issuedToken
    ) {
        String tokenHash =
                refreshTokenHasher.hash(
                        issuedToken.value()
                );

        refreshTokenRepository
                .findByMemberId(memberId)
                .ifPresentOrElse(
                        refreshToken ->
                                refreshToken.rotate(
                                        tokenHash,
                                        issuedToken.expiresAt()
                                ),

                        () -> {
                            Member member =
                                    memberRepository
                                            .getReferenceById(
                                                    memberId
                                            );

                            RefreshToken refreshToken =
                                    RefreshToken.create(
                                            member,
                                            tokenHash,
                                            issuedToken
                                                    .expiresAt()
                                    );

                            refreshTokenRepository.save(
                                    refreshToken
                            );
                        }
                );
    }

    private void validateStoredRefreshToken(
            RefreshToken storedRefreshToken,
            String rawRefreshToken
    ) {
        if (storedRefreshToken.isExpired(
                Instant.now()
        )) {
            refreshTokenRepository.delete(
                    storedRefreshToken
            );

            throw new BusinessException(
                    ErrorCode.EXPIRED_REFRESH_TOKEN
            );
        }

        boolean matches =
                refreshTokenHasher.matches(
                        rawRefreshToken,
                        storedRefreshToken
                                .getTokenHash()
                );

        if (!matches) {
            throw new BusinessException(
                    ErrorCode.INVALID_REFRESH_TOKEN
            );
        }
    }

    private void validateDuplicateEmail(
            String email
    ) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_EMAIL
            );
        }
    }

    private void validateDuplicateNickname(
            String nickname
    ) {
        if (memberRepository.existsByNickname(
                nickname
        )) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_NICKNAME
            );
        }
    }

    private String normalizeEmail(String email) {
        return email
                .strip()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeNickname(
            String nickname
    ) {
        return nickname.strip();
    }
}