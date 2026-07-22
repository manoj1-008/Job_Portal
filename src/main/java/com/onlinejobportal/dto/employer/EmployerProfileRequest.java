package com.onlinejobportal.dto.employer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployerProfileRequest(
        @NotBlank(message = "Company name is required")
        @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
        String companyName,

        @NotBlank(message = "Company description is required")
        String companyDescription,

        @NotBlank(message = "Company website is required")
        String companyWebsite,

        @NotBlank(message = "Company location is required")
        String companyLocation,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be 10-15 digits")
        String phone
) {
}

