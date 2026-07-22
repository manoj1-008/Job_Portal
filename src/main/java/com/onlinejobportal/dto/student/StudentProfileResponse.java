package com.onlinejobportal.dto.student;

import java.time.LocalDateTime;

public record StudentProfileResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String headline,
        String summary,
        String education,
        String skills,
        String experience,
        String linkedinUrl,
        String githubUrl,
        String resumeFileName,
        LocalDateTime createdAt
) {
}

