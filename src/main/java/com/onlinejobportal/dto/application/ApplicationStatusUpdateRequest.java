package com.onlinejobportal.dto.application;

import jakarta.validation.constraints.NotBlank;

public record ApplicationStatusUpdateRequest(
        @NotBlank(message = "Status is required")
        String status
) {
}

