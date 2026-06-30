package com.example.board.global.security;

import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SecurityResponseWriter {

    private final JsonMapper jsonMapper;

    public void write(
            HttpServletResponse response,
            ErrorCode errorCode
    ) throws IOException {

        response.setStatus(
                errorCode.getHttpStatus().value()
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        jsonMapper.writeValue(
                response.getWriter(),
                ApiResponse.failure(errorCode)
        );
    }
}