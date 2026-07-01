package com.example.board.domain.comment.entity;

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
        name = "comment",
        indexes = {
                @Index(
                        name = "idx_comment_post_status_created",
                        columnList = "post_id, status, created_at"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

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
                    name = "fk_comment_post"
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
                    name = "fk_comment_member"
            )
    )
    private Member author;

    @Column(
            nullable = false,
            length = 1000
    )
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private CommentStatus status;

    private Comment(
            Post post,
            Member author,
            String content,
            CommentStatus status
    ) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.status = status;
    }

    public static Comment create(
            Post post,
            Member author,
            String content
    ) {
        return new Comment(
                post,
                author,
                content,
                CommentStatus.PUBLISHED
        );
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }

    public boolean isAuthor(Long memberId) {
        return author.getId().equals(memberId);
    }
}