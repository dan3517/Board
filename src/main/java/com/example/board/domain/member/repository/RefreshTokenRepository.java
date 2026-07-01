package com.example.board.domain.member.repository;

import com.example.board.domain.member.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select refreshToken
            from RefreshToken refreshToken
            join fetch refreshToken.member
            where refreshToken.member.id = :memberId
            """)
    Optional<RefreshToken> findByMemberIdForUpdate(
            @Param("memberId") Long memberId
    );

    void deleteByMemberId(Long memberId);
}