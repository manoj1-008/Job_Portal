package com.onlinejobportal.dto.job;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String title,
        String description,
        String company,
        String location,
        String employmentType,
        String experienceLevel,
        Double salaryMin,
        Double salaryMax,
        String salaryRange,
        String skills,
        LocalDate deadline,
        LocalDateTime createdAt,
        boolean active,
        boolean isExpired,
        Long employerId,
        String employerName,
        String employerEmail,
        int applicationCount,
        boolean hasApplied,
        boolean hasSaved
) {
}

