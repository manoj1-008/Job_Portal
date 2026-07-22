package com.onlinejobportal.dto.application;

import jakarta.validation.constraints.NotNull;

public record ApplyJobRequest(
        @NotNull(message = "Job ID is required")
        Long jobId,

        String coverLetter
) {
}

