package com.onlinejobportal.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateJobRequest(
        @NotBlank(message = "Job title is required")
        @Size(min = 5, max = 200, message = "Job title must be between 5 and 200 characters")
        String title,

        @NotBlank(message = "Job description is required")
        String description,

        @NotBlank(message = "Company name is required")
        @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
        String company,

        @NotBlank(message = "Location is required")
        String location,

        @NotBlank(message = "Employment type is required")
        String employmentType,

        String experienceLevel,

        @Positive(message = "Minimum salary must be positive")
        Double salaryMin,

        @Positive(message = "Maximum salary must be positive")
        Double salaryMax,

        String skills,

        LocalDate deadline,

        boolean active
) {
}

