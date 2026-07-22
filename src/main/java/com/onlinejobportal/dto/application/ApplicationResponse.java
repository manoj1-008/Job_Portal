package com.onlinejobportal.dto.application;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String company,
        String location,
        Long jobSeekerId,
        String jobSeekerName,
        String jobSeekerEmail,
        String status,
        String coverLetter,
        boolean hasResume,
        LocalDateTime appliedAt
) {
}

