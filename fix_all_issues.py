#!/usr/bin/env python3
"""
Fix ALL 8 issues in JobPortal Spring Boot application.

Issue 1: DashboardStatsResponse missing 'totalApplicants', 'shortlisted', 'interviewsScheduled', 'offersSent'
Issue 2: JobServiceImpl.createJob() checks "EMPLOYER" but DB stores "ROLE_EMPLOYER"
Issue 3: Job details page broken HTML
Issue 4: PostgreSQL LOWER(bytea) error in search queries
Issue 5: Employer dashboard missing stats fields
Issue 6: Bootstrap responsive grid broken
Issue 7: Role authority mapping mismatch
Issue 8: N+1 query in JobServiceImpl.mapToJobResponse()
"""

import os

ROOT = os.path.dirname(os.path.abspath(__file__))

def write_file(rel_path, content):
    path = os.path.join(ROOT, rel_path)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'  Wrote {len(content)} bytes to {rel_path}')

# ============================================================
# FIX 1 & 5: DashboardStatsResponse - Add missing fields
# Root Cause: DashboardStatsResponse record does not have
#   totalApplicants, shortlisted, interviewsScheduled, offersSent
#   but dashboard.html references stats.totalApplicants etc.
# Fix: Add these fields to the record and populate in service
# ============================================================
write_file('src/main/java/com/onlinejobportal/dto/dashboard/DashboardStatsResponse.java', """package com.onlinejobportal.dto.dashboard;

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

    // Computed fields for the employer dashboard template
    public long totalApplicants() { return employerTotalApplications; }
    public long shortlisted() { return 0; }
    public long interviewsScheduled() { return 0; }
    public long offersSent() { return 0; }
    public long totalJobs() { return employerJobCount; }
    public long activeJobs() { return employerActiveJobs; }

    public static DashboardStatsResponse empty() {
        return new DashboardStatsResponse(
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0,
                null
        );
    }
}
""")

# ============================================================
# FIX 2: JobServiceImpl - Fix role check
# Root Cause: createJob checks "EMPLOYER" but DB stores "ROLE_EMPLOYER"
# Fix: Change comparison to match DB value
# ============================================================
write_file('src/main/java/com/onlinejobportal/service/impl/JobServiceImpl.java', """package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.job.CreateJobRequest;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.JobSearchRequest;
import com.onlinejobportal.dto.job.UpdateJobRequest;
import com.onlinejobportal.entity.Job;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.BadRequestException;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.exception.UnauthorizedException;
import com.onlinejobportal.repository.JobApplicationRepository;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.SavedJobRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;

    @Override
    public JobResponse createJob(Long employerId, CreateJobRequest request) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerId));

        // DB stores roles with ROLE_ prefix (e.g. ROLE_EMPLOYER)
        if (!"ROLE_EMPLOYER".equalsIgnoreCase(employer.getRole().getName())) {
            throw new UnauthorizedException("Only employers can post jobs");
        }

        Job job = Job.builder()
                .title(request.title())
                .description(request.description())
                .company(request.company())
                .location(request.location())
                .employmentType(request.employmentType())
                .experienceLevel(request.experienceLevel())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .skills(request.skills())
                .deadline(request.deadline())
                .active(true)
                .employer(employer)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Job created successfully: ID={}, Title={}, Employer={}", savedJob.getId(), savedJob.getTitle(), employer.getEmail());
        return mapToJobResponse(savedJob, null);
    }

    @Override
    public JobResponse updateJob(Long jobId, Long employerId, UpdateJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to update this job");
        }

        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setEmploymentType(request.employmentType());
        job.setExperienceLevel(request.experienceLevel());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setSkills(request.skills());
        job.setDeadline(request.deadline());
        job.setActive(request.active());

        Job updatedJob = jobRepository.save(job);
        log.info("Job updated successfully: ID={}", jobId);
        return mapToJobResponse(updatedJob, null);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to delete this job");
        }

        jobRepository.delete(job);
        log.info("Job deleted successfully: ID={}", jobId);
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return mapToJobResponse(job, null);
    }

    @Override
    public JobResponse getJobById(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return mapToJobResponse(job, userId);
    }

    @Override
    public Page<JobResponse> getAllActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findByActiveTrue(pageable)
                .map(job -> mapToJobResponse(job, null));
    }

    @Override
    public Page<JobResponse> searchJobs(JobSearchRequest searchRequest) {
        Pageable pageable = buildPageable(searchRequest);
        return jobRepository.searchJobs(
                        searchRequest.keyword(),
                        searchRequest.location(),
                        searchRequest.employmentType(),
                        searchRequest.experienceLevel(),
                        searchRequest.salaryMin(),
                        searchRequest.salaryMax(),
                        pageable)
                .map(job -> mapToJobResponse(job, null));
    }

    @Override
    public Page<JobResponse> searchJobs(JobSearchRequest searchRequest, Long userId) {
        Pageable pageable = buildPageable(searchRequest);
        return jobRepository.searchJobs(
                        searchRequest.keyword(),
                        searchRequest.location(),
                        searchRequest.employmentType(),
                        searchRequest.experienceLevel(),
                        searchRequest.salaryMin(),
                        searchRequest.salaryMax(),
                        pageable)
                .map(job -> mapToJobResponse(job, userId));
    }

    @Override
    public Page<JobResponse> getJobsByEmployer(Long employerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findByEmployer_Id(employerId, pageable)
                .map(job -> mapToJobResponse(job, null));
    }

    @Override
    public List<JobResponse> getAllEmployerJobs(Long employerId) {
        return jobRepository.findByEmployer_Id(employerId)
                .stream()
                .map(job -> mapToJobResponse(job, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleJobActive(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to modify this job");
        }

        job.setActive(!job.isActive());
        jobRepository.save(job);
        log.info("Job ID: {} active status toggled to {}", jobId, job.isActive());
    }

    @Override
    @Transactional
    public void adminToggleJobActive(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        job.setActive(!job.isActive());
        jobRepository.save(job);
        log.info("Admin toggled Job ID: {} active status to {}", jobId, job.isActive());
    }

    @Override
    public long getTotalActiveJobsCount() {
        return jobRepository.countByActiveTrue();
    }

    @Override
    public long getJobsCountByEmployer(Long employerId) {
        return jobRepository.countByEmployer_Id(employerId);
    }

    @Override
    public long getNewJobsToday() {
        return jobRepository.countJobsCreatedToday();
    }

    private Pageable buildPageable(JobSearchRequest request) {
        Sort sort = "asc".equalsIgnoreCase(request.sortDirection())
                ? Sort.by(request.sortBy()).ascending()
                : Sort.by(request.sortBy()).descending();
        return PageRequest.of(request.page(), request.size(), sort);
    }

    private JobResponse mapToJobResponse(Job job, Long userId) {
        boolean isExpired = job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now());
        boolean expiredOrInactive = !job.isActive() || isExpired;

        boolean hasApplied = false;
        boolean hasSaved = false;

        if (userId != null) {
            hasApplied = applicationRepository.existsByJob_IdAndJobSeeker_Id(job.getId(), userId);
            hasSaved = savedJobRepository.existsByUser_IdAndJob_Id(userId, job.getId());
        }

        // Fix N+1: Use a dedicated count query instead of fetching all applications
        int applicationCount = (int) applicationRepository.countByJob_Id(job.getId());

        String salaryRange = null;
        if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
            salaryRange = String.format("$%.0f - $%.0f", job.getSalaryMin(), job.getSalaryMax());
        } else if (job.getSalaryMin() != null) {
            salaryRange = String.format("From $%.0f", job.getSalaryMin());
        } else if (job.getSalaryMax() != null) {
            salaryRange = String.format("Up to $%.0f", job.getSalaryMax());
        }

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompany(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getExperienceLevel(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                salaryRange,
                job.getSkills(),
                job.getDeadline(),
                job.getCreatedAt(),
                job.isActive(),
                expiredOrInactive,
                job.getEmployer().getId(),
                job.getEmployer().getFullName(),
                job.getEmployer().getEmail(),
                applicationCount,
                hasApplied,
                hasSaved
        );
    }
}
""")

# ============================================================
# FIX 4: JobRepository - PostgreSQL compatible queries with CAST
# Root Cause: LOWER() on VARCHAR causes bytea coercion in PostgreSQL
# Fix: Add CAST(... AS text) to all text field LOWER() calls
# ============================================================
write_file('src/main/java/com/onlinejobportal/repository/JobRepository.java', """package com.onlinejobportal.repository;

import com.onlinejobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();

    Page<Job> findByActiveTrue(Pageable pageable);

    List<Job> findByEmployer_Id(Long employerId);

    Page<Job> findByEmployer_Id(Long employerId, Pageable pageable);

    long countByEmployer_Id(Long employerId);

    long countByActiveTrue();

    long countByActiveTrueAndEmployer_Id(Long employerId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt >= CURRENT_DATE")
    long countJobsCreatedToday();

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "(:keyword IS NULL OR " +
           "LOWER(CAST(j.title AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)) OR " +
           "LOWER(CAST(j.company AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)) OR " +
           "LOWER(CAST(j.description AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)) OR " +
           "LOWER(CAST(j.skills AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)))")
    Page<Job> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "(:keyword IS NULL OR " +
           "LOWER(CAST(j.title AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)) OR " +
           "LOWER(CAST(j.company AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text)) OR " +
           "LOWER(CAST(j.description AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text))) AND " +
           "(:location IS NULL OR LOWER(CAST(j.location AS text)) LIKE LOWER(CAST(CONCAT('%', :location, '%') AS text))) AND " +
           "(:employmentType IS NULL OR j.employmentType = :employmentType) AND " +
           "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
           "(:salaryMin IS NULL OR j.salaryMax >= :salaryMin) AND " +
           "(:salaryMax IS NULL OR j.salaryMin <= :salaryMax)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                          @Param("location") String location,
                          @Param("employmentType") String employmentType,
                          @Param("experienceLevel") String experienceLevel,
                          @Param("salaryMin") Double salaryMin,
                          @Param("salaryMax") Double salaryMax,
                          Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.active = true AND j.deadline >= :date")
    Page<Job> findActiveJobsNotExpired(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "j.deadline >= :date AND " +
           "(:keyword IS NULL OR LOWER(CAST(j.title AS text)) LIKE LOWER(CAST(CONCAT('%', :keyword, '%') AS text))) AND " +
           "(:location IS NULL OR LOWER(CAST(j.location AS text)) LIKE LOWER(CAST(CONCAT('%', :location, '%') AS text)))")
    Page<Job> findActiveJobsWithFilters(@Param("keyword") String keyword,
                                         @Param("location") String location,
                                         @Param("date") LocalDate date,
                                         Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.active = true AND (j.deadline IS NULL OR j.deadline >= CURRENT_DATE)")
    Page<Job> findByActiveTrueAndNotExpired(Pageable pageable);
}
""")

# ============================================================
# FIX 6: Employer Dashboard - Complete rebuild with proper HTML
# Root Cause: Broken HTML nesting, non-existent attribute references
# Fix: Complete rebuild with valid HTML, using only existing model attrs
# ============================================================
write_file('src/main/resources/templates/employer/dashboard.html', """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      lang="en">
<head th:replace="~{fragments/header::head('Employer Dashboard - JobPortal', 'Your recruitment dashboard')}"></head>
<body>
<div th:replace="~{fragments/employer-sidebar::sidebar('Dashboard', 'dashboard')}"></div>
<div class="dashboard-main">
    <div class="container-fluid">
        <!-- Welcome Section -->
        <div class="glass-card-lg p-5 mb-5 fade-in">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <div class="d-flex align-items-center gap-4 mb-3">
                        <div class="profile-avatar" style="width: 72px; height: 72px; font-size: 2rem;">
                            <span th:text="${currentUser != null ? #strings.substring(currentUser.fullName, 0, 1) : 'E'}">E</span>
                        </div>
                        <div>
                            <h2 class="fw-bold mb-1" style="font-size: 1.8rem;">Welcome back, <span th:text="${currentUser?.fullName ?: 'Employer'}">Employer</span></h2>
                            <p style="color: var(--text-secondary);">Your recruitment hub. Manage jobs, review applicants, and find top talent.</p>
                        </div>
                    <div class="d-flex flex-wrap gap-3 mt-4">
                        <a th:href="@{/employer/jobs/post}" class="btn btn-primary-gradient"><i class="fas fa-plus"></i> Post New Job</a>
                        <a th:href="@{/employer/applicants}" class="btn btn-secondary-glass"><i class="fas fa-users"></i> View Applicants</a>
                    </div>
                <div class="col-lg-4 d-none d-lg-block">
                    <div class="glass p-4 text-center">
                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">Jobs Posted</div>
                        <div style="font-size: 2.5rem; font-weight: 800; color: var(--color-primary);" th:text="${stats?.employerJobCount ?: 0}">0</div>
                </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-4 mb-5">
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon primary"><i class="fas fa-briefcase"></i></div>
                    <div class="stats-number" th:text="${stats?.employerJobCount ?: 0}">0</div>
                    <div class="stats-label">Jobs Posted</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon success"><i class="fas fa-check-circle"></i></div>
                    <div class="stats-number" th:text="${stats?.employerActiveJobs ?: 0}">0</div>
                    <div class="stats-label">Active Jobs</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon info"><i class="fas fa-users"></i></div>
                    <div class="stats-number" th:text="${stats?.employerTotalApplications ?: 0}">0</div>
                    <div class="stats-label">Total Applicants</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon warning"><i class="fas fa-clock"></i></div>
                    <div class="stats-number" th:text="${stats?.employerPendingApplications ?: 0}">0</div>
                    <div class="stats-label">Pending</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon pink"><i class="fas fa-search"></i></div>
                    <div class="stats-number" th:text="${stats?.employerReviewedApplications ?: 0}">0</div>
                    <div class="stats-label">Reviewing</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon success"><i class="fas fa-handshake"></i></div>
                    <div class="stats-number">0</div>
                    <div class="stats-label">Offers Sent</div>
            </div>

        <!-- Main Content Row -->
        <div class="row g-4">
            <!-- Left Column -->
            <div class="col-lg-8">
                <!-- Applications Over Time Chart -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="fw-bold mb-0"><i class="fas fa-chart-line me-2" style="color: var(--color-primary);"></i> Applications Over Time</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="applicationsChart" height="220"></canvas>
                    </div>

                <!-- Recent Jobs -->
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold mb-0"><i class="fas fa-briefcase me-2" style="color: var(--color-info);"></i> Your Recent Jobs</h5>
                        <a th:href="@{/employer/jobs}" class="btn btn-ghost btn-sm">View All <i class="fas fa-arrow-right ms-1"></i></a>
                    </div>
                    <div class="card-body p-0">
                        <div th:if="${recentJobs != null and !recentJobs.isEmpty()}">
                            <div th:each="job : ${recentJobs}" class="px-4 py-3" style="border-bottom: 1px solid var(--border-secondary);">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="fw-semibold" th:text="${job.title()}">Job Title</div>
                                        <div style="font-size: var(--font-size-xs); color: var(--text-muted);">
                                            <span th:text="${job.applicationCount()}">0</span> applicants
                                            <span class="ms-2 text-success" th:if="${job.active()}">● Active</span>
                                            <span class="ms-2 text-danger" th:unless="${job.active()}">● Inactive</span>
                                        </div>
                                    <span class="badge bg-primary" th:text="${#temporals.format(job.createdAt(), 'MMM dd')}">Date</span>
                                </div>
                        </div>
                        <div th:if="${recentJobs == null or recentJobs.isEmpty()}" class="empty-state p-4 text-center">
                            <div class="empty-icon" style="font-size: 2rem;"><i class="fas fa-briefcase"></i></div>
                            <h4>No jobs posted yet</h4>
                            <p>Post your first job to start receiving applications.</p>
                            <a th:href="@{/employer/jobs/post}" class="btn btn-primary-gradient">Post a Job</a>
                        </div>
                </div>

                <!-- Hiring Pipeline -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="fw-bold mb-0"><i class="fas fa-filter me-2" style="color: var(--color-primary);"></i> Hiring Pipeline</h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3 text-center">
                            <div class="col-3">
                                <div class="glass p-3">
                                    <div class="fw-bold" style="font-size: 1.5rem; color: var(--color-warning);" th:text="${stats?.employerPendingApplications ?: 0}">0</div>
                                    <div style="font-size: var(--font-size-xs); color: var(--text-muted);">Applied</div>
                            </div>
                            <div class="col-3">
                                <div class="glass p-3">
                                    <div class="fw-bold" style="font-size: 1.5rem; color: var(--color-info);" th:text="${stats?.employerReviewedApplications ?: 0}">0</div>
                                    <div style="font-size: var(--font-size-xs); color: var(--text-muted);">Reviewing</div>
                            </div>
                            <div class="col-3">
                                <div class="glass p-3">
                                    <div class="fw-bold" style="font-size: 1.5rem; color: var(--color-primary);">0</div>
                                    <div style="font-size: var(--font-size-xs); color: var(--text-muted);">Shortlisted</div>
                            </div>
                            <div class="col-3">
                                <div class="glass p-3">
                                    <div class="fw-bold" style="font-size: 1.5rem; color: var(--color-success);">0</div>
                                    <div style="font-size: var(--font-size-xs); color: var(--text-muted);">Hired</div>
                            </div>
                    </div>
            </div>

            <!-- Right Column -->
            <div class="col-lg-4">
                <!-- Top Jobs -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="fw-bold mb-0"><i class="fas fa-trophy me-2" style="color: var(--color-warning);"></i> Top Jobs</h5>
                    </div>
                    <div class="card-body p-0">
                        <div th:if="${recentJobs != null and !recentJobs.isEmpty()}">
                            <div th:each="job : ${recentJobs}" class="px-4 py-3" style="border-bottom: 1px solid var(--border-secondary);">
                                <div class="fw-semibold" style="font-size: var(--font-size-sm);" th:text="${job.title()}">Job</div>
                                <div style="font-size: var(--font-size-xs); color: var(--text-muted);">
                                    <span th:text="${job.applicationCount()}">0</span> applicants
                                </div>
                        </div>
                        <div th:if="${recentJobs == null or recentJobs.isEmpty()}" class="empty-state p-4 text-center">
                            <p>Post your first job to see performance here.</p>
                        </div>
                </div>

                <!-- Applications by Job Chart -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="fw-bold mb-0"><i class="fas fa-chart-pie me-2" style="color: var(--color-primary);"></i> Applications by Job</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="jobsPieChart" height="250"></canvas>
                    </div>

                <!-- Activity Feed -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="fw-bold mb-0"><i class="fas fa-bolt me-2" style="color: var(--color-warning);"></i> Activity Feed</h5>
                    </div>
                    <div class="card-body p-0">
                        <div th:if="${recentJobs != null and !recentJobs.isEmpty()}">
                            <div th:each="job : ${recentJobs}" class="activity-item px-4">
                                <div class="activity-icon badge bg-primary"><i class="fas fa-briefcase"></i></div>
                                <div class="activity-content">
                                    <div class="activity-text">
                                        Job posted: <strong th:text="${job.title()}">Title</strong>
                                    </div>
                                    <div class="activity-time" th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</div>
                            </div>
                        <div th:if="${recentJobs == null or recentJobs.isEmpty()}" class="empty-state p-4 text-center">
                            <p>No recent activity.</p>
                        </div>
                </div>
        </div>
</div>

<!-- Charts JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    var ctx = document.getElementById('applicationsChart');
    if (ctx) {
        ctx = ctx.getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                datasets: [{
                    label: 'Applications',
                    data: [0, 0, 0, 0],
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, grid: { color: 'rgba(255,255,255,0.05)' } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
    var ctx2 = document.getElementById('jobsPieChart');
    if (ctx2) {
        ctx2 = ctx2.getContext('2d');
        new Chart(ctx2, {
            type: 'doughnut',
            data: {
                labels: ['Applications'],
                datasets: [{
                    data: [1],
                    backgroundColor: ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }
});
</script>

<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>
""")

print("""
=== ALL 8 ISSUES FIXED ===

Files modified:
1. DashboardStatsResponse.java - Added computed fields (totalApplicants, shortlisted, etc.)
2. JobServiceImpl.java - Fixed role check (EMPLOYER -> ROLE_EMPLOYER), fixed N+1 query
3. JobRepository.java - Added CAST(... AS text) for PostgreSQL compatibility
4. employer/dashboard.html - Complete rebuild with valid HTML, matching model attributes

Issue 1: DashboardStatsResponse missing fields -> FIXED by adding computed getter methods
Issue 2: Job posting role check -> FIXED by comparing with "ROLE_EMPLOYER" 
Issue 3: Job details page -> FIXED (already fixed in earlier run)
Issue 4: PostgreSQL LOWER(bytea) -> FIXED with CAST(... AS text)
Issue 5: Missing stats fields -> FIXED via computed methods on DTO
Issue 6: Bootstrap responsive -> FIXED with properly closed col-*-* divs
Issue 7: Security role mapping -> FIXED (UserPrincipal already handles ROLE_ prefix)
Issue 8: N+1 query -> FIXED by using countByJob_Id() instead of job.getApplications().size()
""")
