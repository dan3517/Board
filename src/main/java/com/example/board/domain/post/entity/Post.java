package com.example.board.domain.post.entity;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.member.entity.Member;
import com.example.board.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_post_member"
            )
    )
    private Member author;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_post_category"
            )
    )
    private Category category;

    @Column(
            nullable = false,
            length = 100
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(nullable = false)
    private long viewCount;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private PostStatus status;

    private Post(
            Member author,
            Category category,
            String title,
            String content,
            long viewCount,
            PostStatus status
    ) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.status = status;
    }

    public static Post create(
            Member author,
            Category category,
            String title,
            String content
    ) {
        return new Post(
                author,
                category,
                title,
                content,
                0L,
                PostStatus.PUBLISHED
        );
    }

    public void update(
            Category category,
            String title,
            String content
    ) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    public boolean isAuthor(Long memberId) {
        return author.getId().equals(memberId);
    }

    public boolean isPublished() {
        return status == PostStatus.PUBLISHED;
    }
}