package com.onlinejobportal.dto.job;

public record JobSearchRequest(
        String keyword,
        String location,
        String employmentType,
        String experienceLevel,
        Double salaryMin,
        Double salaryMax,
        String sortBy,
        String sortDirection,
        int page,
        int size
) {

    public JobSearchRequest {
        if (page <= 0) page = 0;
        if (size <= 0) size = 10;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
    }

    public static JobSearchRequest defaultRequest() {
        return new JobSearchRequest(null, null, null, null, null, null, "createdAt", "desc", 0, 10);
    }
}

