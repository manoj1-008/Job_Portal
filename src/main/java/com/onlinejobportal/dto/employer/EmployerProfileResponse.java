package com.onlinejobportal.dto.employer;

import java.time.LocalDateTime;

public record EmployerProfileResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String companyName,
        String companyDescription,
        String companyWebsite,
        String companyLocation,
        int activeJobCount,
        int totalApplicants,
        LocalDateTime createdAt
) {
}

