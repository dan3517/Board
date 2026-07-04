package com.example.board.domain.member.entity;

import com.example.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_member_nickname",
                        columnNames = "nickname"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    private Member(
            String email,
            String password,
            String nickname,
            MemberRole role,
            MemberStatus status
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    public static Member create(
            String email,
            String encodedPassword,
            String nickname
    ) {
        return new Member(
                email,
                encodedPassword,
                nickname,
                MemberRole.USER,
                MemberStatus.ACTIVE
        );
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void suspend() {
        this.status = MemberStatus.SUSPENDED;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}