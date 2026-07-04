package com.example.board.domain.member.repository;

import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndStatus(
            String email,
            MemberStatus status
    );

    Optional<Member> findByIdAndStatus(
            Long id,
            MemberStatus status
    );
}