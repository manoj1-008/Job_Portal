package com.onlinejobportal.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentProfileRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be 10-15 digits")
        String phone,

        String headline,

        String summary,

        String education,

        String skills,

        String experience,

        String linkedinUrl,

        String githubUrl
) {
}

