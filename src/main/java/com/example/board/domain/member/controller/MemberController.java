package com.example.board.domain.member.controller;

import com.example.board.domain.member.controller.docs.MemberApiDocs;
import com.example.board.domain.member.dto.response.MyProfileResponse;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApiDocs {

    @Override
    @GetMapping("/me")
    public ApiResponse<MyProfileResponse> getMyProfile(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
                MyProfileResponse.from(userDetails)
        );
    }
}