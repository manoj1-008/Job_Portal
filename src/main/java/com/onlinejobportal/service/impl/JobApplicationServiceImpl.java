package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.application.ApplicationResponse;
import com.onlinejobportal.dto.application.ApplicationStatusUpdateRequest;
import com.onlinejobportal.dto.application.ApplyJobRequest;
import com.onlinejobportal.entity.Job;
import com.onlinejobportal.entity.JobApplication;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.BadRequestException;
import com.onlinejobportal.exception.DuplicateResourceException;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.exception.UnauthorizedException;
import com.onlinejobportal.repository.JobApplicationRepository;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public ApplicationResponse applyForJob(Long jobSeekerId, ApplyJobRequest request) {
        User jobSeeker = userRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", jobSeekerId));

        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", request.jobId()));

        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer accepting applications");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("The application deadline for this job has passed");
        }

        if (applicationRepository.existsByJob_IdAndJobSeeker_Id(job.getId(), jobSeekerId)) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .jobSeeker(jobSeeker)
                .status(JobApplication.ApplicationStatus.PENDING)
                .coverLetter(request.coverLetter())
                .build();

        JobApplication savedApplication = applicationRepository.save(application);
        log.info("Job application submitted: Job={}, Seeker={}", job.getId(), jobSeeker.getEmail());
        return mapToApplicationResponse(savedApplication);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, Long jobSeekerId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        if (!application.getJobSeeker().getId().equals(jobSeekerId)) {
            throw new UnauthorizedException("You are not authorized to withdraw this application");
        }

        applicationRepository.delete(application);
        log.info("Application withdrawn: ID={}", applicationId);
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByJobSeeker(Long jobSeekerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return applicationRepository.findByJobSeeker_Id(jobSeekerId, pageable)
                .map(this::mapToApplicationResponse);
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByJob(Long jobId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return applicationRepository.findByJob_Id(jobId, pageable)
                .map(this::mapToApplicationResponse);
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByEmployer(Long employerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return applicationRepository.findByEmployerId(employerId, pageable)
                .map(this::mapToApplicationResponse);
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByEmployerAndStatus(Long employerId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        JobApplication.ApplicationStatus applicationStatus = JobApplication.ApplicationStatus.valueOf(status.toUpperCase());
        return applicationRepository.findByEmployerIdAndStatus(employerId, applicationStatus, pageable)
                .map(this::mapToApplicationResponse);
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        JobApplication.ApplicationStatus newStatus;
        try {
            newStatus = JobApplication.ApplicationStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid application status: " + request.status());
        }

        application.setStatus(newStatus);
        JobApplication updatedApplication = applicationRepository.save(application);
        log.info("Application ID: {} status updated to {}", applicationId, newStatus);
        return mapToApplicationResponse(updatedApplication);
    }

    @Override
    public ApplicationResponse getApplicationById(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        return mapToApplicationResponse(application);
    }

    @Override
    public boolean hasApplied(Long jobId, Long userId) {
        return applicationRepository.existsByJob_IdAndJobSeeker_Id(jobId, userId);
    }

    @Override
    public long getApplicationCountByJob(Long jobId) {
        return applicationRepository.countByJob_Id(jobId);
    }

    @Override
    public long getApplicationCountByEmployer(Long employerId) {
        return applicationRepository.countByJob_Employer_Id(employerId);
    }

    @Override
    public long getActiveApplicationCountBySeeker(Long seekerId) {
        return applicationRepository.countActiveApplicationsBySeekerId(seekerId);
    }

    private ApplicationResponse mapToApplicationResponse(JobApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompany(),
                application.getJob().getLocation(),
                application.getJobSeeker().getId(),
                application.getJobSeeker().getFullName(),
                application.getJobSeeker().getEmail(),
                application.getStatus().name(),
                application.getCoverLetter(),
                application.getJobSeeker().getResume() != null,
                application.getAppliedAt()
        );
    }
}

