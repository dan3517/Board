package com.example.board.domain.postlike.service;

import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.postlike.dto.response.PostLikeResponse;
import com.example.board.domain.postlike.entity.PostLike;
import com.example.board.domain.postlike.repository.PostLikeRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostLikeResponse likePost(
            Long memberId,
            Long postId
    ) {
        Post post = findPublishedPostForUpdate(postId);
        Member member = findActiveMember(memberId);

        boolean alreadyLiked =
                postLikeRepository
                        .existsByPostIdAndMemberId(
                                postId,
                                memberId
                        );

        if (!alreadyLiked) {
            PostLike postLike =
                    PostLike.create(post, member);

            postLikeRepository.save(postLike);
        }

        long likeCount =
                postLikeRepository.countByPostId(
                        postId
                );

        return PostLikeResponse.liked(
                postId,
                likeCount
        );
    }

    public PostLikeResponse unlikePost(
            Long memberId,
            Long postId
    ) {
        findPublishedPostForUpdate(postId);

        postLikeRepository
                .deleteByPostIdAndMemberId(
                        postId,
                        memberId
                );

        long likeCount =
                postLikeRepository.countByPostId(
                        postId
                );

        return PostLikeResponse.unliked(
                postId,
                likeCount
        );
    }

    private Post findPublishedPostForUpdate(
            Long postId
    ) {
        return postRepository
                .findByIdAndStatusForUpdate(
                        postId,
                        PostStatus.PUBLISHED
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.POST_NOT_FOUND
                        )
                );
    }

    private Member findActiveMember(Long memberId) {
        return memberRepository
                .findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }
}