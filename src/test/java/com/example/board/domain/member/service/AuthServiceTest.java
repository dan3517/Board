package com.example.board.domain.member.service;

import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                memberRepository,
                passwordEncoder
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
}