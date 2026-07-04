package com.example.board.domain.member.service;

import com.example.board.domain.member.dto.request.LoginRequest;
import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.TokenResponse;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.RefreshToken;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.member.repository.RefreshTokenRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.global.security.JwtTokenProvider;
import com.example.board.global.security.RefreshTokenHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                memberRepository,
                refreshTokenRepository,
                passwordEncoder,
                authenticationManager,
                jwtTokenProvider,
                refreshTokenHasher
        );
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void signupSuccess() {
        // given
        SignupRequest request = new SignupRequest(
                "USER@EXAMPLE.COM ",
                "Password123!",
                " backend "
        );

        given(
                memberRepository.existsByEmail(
                        "user@example.com"
                )
        ).willReturn(false);

        given(
                memberRepository.existsByNickname(
                        "backend"
                )
        ).willReturn(false);

        given(
                passwordEncoder.encode("Password123!")
        ).willReturn("encoded-password");

        Member savedMember = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        ReflectionTestUtils.setField(
                savedMember,
                "id",
                1L
        );

        given(
                memberRepository.save(any(Member.class))
        ).willReturn(savedMember);

        // when
        SignupResponse response =
                authService.signup(request);

        // then
        assertThat(response.memberId()).isEqualTo(1L);

        ArgumentCaptor<Member> memberCaptor =
                ArgumentCaptor.forClass(Member.class);

        then(memberRepository)
                .should()
                .save(memberCaptor.capture());

        Member member = memberCaptor.getValue();

        assertThat(member.getEmail())
                .isEqualTo("user@example.com");

        assertThat(member.getNickname())
                .isEqualTo("backend");

        assertThat(member.getPassword())
                .isEqualTo("encoded-password");
    }

    @Test
    @DisplayName("이메일이 중복되면 회원가입에 실패한다")
    void signupFailsWhenEmailDuplicated() {
        // given
        SignupRequest request = new SignupRequest(
                "user@example.com",
                "Password123!",
                "backend"
        );

        given(
                memberRepository.existsByEmail(
                        "user@example.com"
                )
        ).willReturn(true);

        // when
        Throwable throwable = catchThrowable(
                () -> authService.signup(request)
        );

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class);

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_EMAIL);

        then(memberRepository)
                .should(never())
                .save(any(Member.class));

        then(passwordEncoder)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("닉네임이 중복되면 회원가입에 실패한다")
    void signupFailsWhenNicknameDuplicated() {
        // given
        SignupRequest request = new SignupRequest(
                "user@example.com",
                "Password123!",
                "backend"
        );

        given(
                memberRepository.existsByEmail(
                        "user@example.com"
                )
        ).willReturn(false);

        given(
                memberRepository.existsByNickname(
                        "backend"
                )
        ).willReturn(true);

        // when
        Throwable throwable = catchThrowable(
                () -> authService.signup(request)
        );

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class);

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

        then(memberRepository)
                .should(never())
                .save(any(Member.class));

        then(passwordEncoder)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("로그인에 성공하면 Access Token과 Refresh Token을 발급한다")
    void loginSuccess() {
        // given
        LoginRequest request = new LoginRequest(
                "USER@EXAMPLE.COM ",
                "Password123!"
        );

        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        ReflectionTestUtils.setField(
                member,
                "id",
                1L
        );

        CustomUserDetails principal =
                CustomUserDetails.from(member);

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        given(
                authenticationManager.authenticate(
                        any(Authentication.class)
                )
        ).willReturn(authentication);

        Instant accessExpiresAt =
                Instant.parse("2026-06-30T10:30:00Z");

        Instant refreshExpiresAt =
                Instant.parse("2026-07-30T10:00:00Z");

        JwtTokenProvider.IssuedToken accessToken =
                new JwtTokenProvider.IssuedToken(
                        "access-token",
                        accessExpiresAt
                );

        JwtTokenProvider.IssuedToken refreshToken =
                new JwtTokenProvider.IssuedToken(
                        "refresh-token",
                        refreshExpiresAt
                );

        given(
                jwtTokenProvider.issueTokenPair(1L)
        ).willReturn(
                new JwtTokenProvider.TokenPair(
                        accessToken,
                        refreshToken
                )
        );

        given(
                refreshTokenHasher.hash("refresh-token")
        ).willReturn("hashed-refresh-token");

        given(
                refreshTokenRepository.findByMemberId(1L)
        ).willReturn(java.util.Optional.empty());

        given(
                memberRepository.getReferenceById(1L)
        ).willReturn(member);

        // when
        TokenResponse response =
                authService.login(request);

        // then
        assertThat(response.accessToken())
                .isEqualTo("access-token");

        assertThat(response.refreshToken())
                .isEqualTo("refresh-token");

        assertThat(response.tokenType())
                .isEqualTo("Bearer");

        then(authenticationManager)
                .should()
                .authenticate(
                        argThat(auth ->
                                "user@example.com".equals(
                                        auth.getName()
                                )
                                        &&
                                        "Password123!".equals(
                                                auth.getCredentials()
                                        )
                        )
                );

        then(jwtTokenProvider)
                .should()
                .issueTokenPair(1L);

        then(refreshTokenRepository)
                .should()
                .save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 정보가 일치하지 않으면 실패한다")
    void loginFailsWhenCredentialsInvalid() {
        // given
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "WrongPassword!"
        );

        given(
                authenticationManager.authenticate(
                        any(Authentication.class)
                )
        ).willThrow(
                new org.springframework.security.authentication
                        .BadCredentialsException(
                        "Bad credentials"
                )
        );

        // when
        Throwable throwable = catchThrowable(
                () -> authService.login(request)
        );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.INVALID_LOGIN
                );

        then(jwtTokenProvider)
                .shouldHaveNoInteractions();
    }
}