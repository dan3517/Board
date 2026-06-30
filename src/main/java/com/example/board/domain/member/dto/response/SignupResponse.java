package com.example.board.domain.member.dto.response;

import com.example.board.domain.member.entity.Member;

public record SignupResponse(
        Long memberId
) {

    public static SignupResponse from(Member member) {
        return new SignupResponse(member.getId());
    }
}