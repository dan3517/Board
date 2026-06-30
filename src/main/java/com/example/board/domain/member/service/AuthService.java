package com.example.board.domain.member.service;

import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        String nickname = normalizeNickname(request.nickname());

        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);

        String encodedPassword =
                passwordEncoder.encode(request.password());

        Member member = Member.create(
                email,
                encodedPassword,
                nickname
        );

        Member savedMember = memberRepository.save(member);

        return SignupResponse.from(savedMember);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_EMAIL
            );
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
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

    private String normalizeNickname(String nickname) {
        return nickname.strip();
    }
}