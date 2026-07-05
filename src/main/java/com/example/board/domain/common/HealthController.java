package com.example.board.domain.common;

import com.example.board.domain.common.docs.HealthApiDocs;
import com.example.board.global.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController implements HealthApiDocs {

    @Override
    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.success("board server is running v2");
    }
}