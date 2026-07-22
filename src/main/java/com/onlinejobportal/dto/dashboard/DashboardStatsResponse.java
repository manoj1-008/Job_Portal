package com.onlinejobportal.dto.dashboard;

public record DashboardStatsResponse(
        // Admin stats
        long totalUsers,
        long totalEmployers,
        long totalJobSeekers,
        long totalJobs,
        long activeJobs,
        long totalApplications,
        long newUsersToday,
        long newJobsToday,

        // Employer stats
        long employerJobCount,
        long employerActiveJobs,
        long employerTotalApplications,
        long employerPendingApplications,
        long employerReviewedApplications,

        // Job Seeker stats
        long seekerApplicationsSent,
        long seekerSavedJobs,
        long seekerActiveApplications,
        String resumeStatus
) {

    public static DashboardStatsResponse empty() {
    return new DashboardStatsResponse(
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0,
            null
    );
}
}

