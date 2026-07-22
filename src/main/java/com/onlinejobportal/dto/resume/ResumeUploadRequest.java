package com.onlinejobportal.dto.resume;

public record ResumeUploadRequest(
        String fileName,
        String fileType,
        long fileSize
) {
}

