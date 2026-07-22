package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;
import com.onlinejobportal.entity.JobApplication;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.repository.JobApplicationRepository;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.ResumeRepository;
import com.onlinejobportal.repository.SavedJobRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public DashboardStatsResponse getAdminDashboardStats() {

        long totalUsers = userRepository.count();
long totalEmployers = userRepository.countByRole_Name("ROLE_EMPLOYER");
        long totalJobSeekers = userRepository.countByRole_Name("ROLE_JOBSEEKER");
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByActiveTrue();
        long totalApplications = applicationRepository.count();
        long newUsersToday = userRepository.countUsersCreatedToday();
        long newJobsToday = jobRepository.countJobsCreatedToday();

        return new DashboardStatsResponse(
                totalUsers,
                totalEmployers,
                totalJobSeekers,
                totalJobs,
                activeJobs,
                totalApplications,
                newUsersToday,
                newJobsToday,

                // Employer Stats
                0,
                0,
                0,
                0,
                0,

                // Job Seeker Stats
                0,
                0,
                0,

                null
        );
    }

    @Override
    public DashboardStatsResponse getEmployerDashboardStats(Long employerId) {

        User employer = userRepository.findById(employerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", employerId));

        long employerJobCount =
                jobRepository.countByEmployer_Id(employerId);

        long employerActiveJobs =
                jobRepository.countByActiveTrueAndEmployer_Id(employerId);

        long employerTotalApplications =
                applicationRepository.countByJob_Employer_Id(employerId);

        long employerPendingApplications =
                applicationRepository.countByStatusAndEmployerId(
                        JobApplication.ApplicationStatus.PENDING,
                        employerId
                );

        long employerReviewedApplications =
                applicationRepository.countByStatusAndEmployerId(
                        JobApplication.ApplicationStatus.REVIEWING,
                        employerId
                );

        return new DashboardStatsResponse(

                // Admin Stats
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,

                // Employer Stats
                employerJobCount,
                employerActiveJobs,
                employerTotalApplications,
                employerPendingApplications,
                employerReviewedApplications,

                // Job Seeker Stats
                0,
                0,
                0,

                null
        );
    }

    @Override
    public DashboardStatsResponse getJobSeekerDashboardStats(Long seekerId) {

        if (!userRepository.existsById(seekerId)) {
            throw new ResourceNotFoundException("User", "id", seekerId);
        }

        long seekerApplicationsSent =
                applicationRepository.countByJobSeeker_Id(seekerId);

        long seekerSavedJobs =
                savedJobRepository.countByUser_Id(seekerId);

        long seekerActiveApplications =
                applicationRepository.countActiveApplicationsBySeekerId(seekerId);

        String resumeStatus =
                resumeRepository.existsByUser_Id(seekerId)
                        ? "UPLOADED"
                        : "NOT_UPLOADED";

        return new DashboardStatsResponse(

                // Admin Stats
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,

                // Employer Stats
                0,
                0,
                0,
                0,
                0,

                // Job Seeker Stats
                seekerApplicationsSent,
                seekerSavedJobs,
                seekerActiveApplications,

                resumeStatus
        );
    }
}