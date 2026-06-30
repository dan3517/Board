package com.example.board.domain.member.dto.response;

import com.example.board.domain.member.entity.MemberRole;
import com.example.board.global.security.CustomUserDetails;

public record MyProfileResponse(
        Long memberId,
        String email,
        String nickname,
        MemberRole role
) {

    public static MyProfileResponse from(
            CustomUserDetails userDetails
    ) {
        return new MyProfileResponse(
                userDetails.getMemberId(),
                userDetails.getEmail(),
                userDetails.getNickname(),
                userDetails.getRole()
        );
    }
}