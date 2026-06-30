package com.example.board.global.security;

import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail = email
                .strip()
                .toLowerCase(Locale.ROOT);

        Member member = memberRepository
                .findByEmailAndStatus(
                        normalizedEmail,
                        MemberStatus.ACTIVE
                )
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        return CustomUserDetails.from(member);
    }

    public CustomUserDetails loadUserByMemberId(Long memberId) {
        Member member = memberRepository
                .findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        return CustomUserDetails.from(member);
    }
}