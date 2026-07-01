package com.example.board.domain.postlike.entity;

import com.example.board.domain.member.entity.Member;
import com.example.board.domain.post.entity.Post;
import com.example.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_like_post_member",
                        columnNames = {
                                "post_id",
                                "member_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_post_like_post",
                        columnList = "post_id"
                ),
                @Index(
                        name = "idx_post_like_member",
                        columnList = "member_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "post_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_post_like_post"
            )
    )
    private Post post;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_post_like_member"
            )
    )
    private Member member;

    private PostLike(
            Post post,
            Member member
    ) {
        this.post = post;
        this.member = member;
    }

    public static PostLike create(
            Post post,
            Member member
    ) {
        return new PostLike(post, member);
    }
}