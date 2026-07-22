package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.job.CreateJobRequest;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.JobSearchRequest;
import com.onlinejobportal.dto.job.UpdateJobRequest;
import com.onlinejobportal.entity.Job;
import com.onlinejobportal.entity.User;
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
