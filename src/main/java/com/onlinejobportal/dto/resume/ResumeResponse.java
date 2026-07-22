package com.onlinejobportal.dto.resume;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        String fileName,
        String fileType,
        long fileSize,
        LocalDateTime uploadedAt,
        String downloadUrl
) {
}

