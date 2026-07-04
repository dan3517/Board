package com.example.board.global.swagger.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(
        name = "PostImageUploadRequest",
        description = "게시글 이미지 업로드 요청"
)
public final class PostImageUploadSwaggerRequest {

    @ArraySchema(
            arraySchema = @Schema(
                    description = """
                            업로드할 이미지 파일 목록입니다.
                            JPEG, PNG, WebP 형식을 지원합니다.
                            게시글 하나당 최대 5개까지 첨부할 수 있습니다.
                            """
            ),
            schema = @Schema(
                    type = "string",
                    format = "binary"
            ),
            minItems = 1,
            maxItems = 5
    )
    private List<MultipartFile> files;

    public List<MultipartFile> getFiles() {
        return files;
    }
}