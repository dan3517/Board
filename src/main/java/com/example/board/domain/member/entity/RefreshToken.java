package com.example.board.domain.member.entity;

import com.example.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(
        name = "refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_member",
                        columnNames = "member_id"
                ),
                @UniqueConstraint(
                        name = "uk_refresh_token_hash",
                        columnNames = "token_hash"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_refresh_token_member"
            )
    )
    private Member member;

    @Column(
            name = "token_hash",
            nullable = false,
            length = 64
    )
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private RefreshToken(
            Member member,
            String tokenHash,
            Instant expiresAt
    ) {
        this.member = member;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(
            Member member,
            String tokenHash,
            Instant expiresAt
    ) {
        return new RefreshToken(
                member,
                tokenHash,
                expiresAt
        );
    }

    public void rotate(
            String tokenHash,
            Instant expiresAt
    ) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now)
                || expiresAt.equals(now);
    }
}