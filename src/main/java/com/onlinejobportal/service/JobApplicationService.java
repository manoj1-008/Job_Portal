package com.onlinejobportal.service;

import com.onlinejobportal.dto.application.ApplicationResponse;
import com.onlinejobportal.dto.application.ApplicationStatusUpdateRequest;
import com.onlinejobportal.dto.application.ApplyJobRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobApplicationService {

    ApplicationResponse applyForJob(Long jobSeekerId, ApplyJobRequest request);

    void withdrawApplication(Long applicationId, Long jobSeekerId);

    Page<ApplicationResponse> getApplicationsByJobSeeker(Long jobSeekerId, int page, int size);

    Page<ApplicationResponse> getApplicationsByJob(Long jobId, int page, int size);

    Page<ApplicationResponse> getApplicationsByEmployer(Long employerId, int page, int size);

    Page<ApplicationResponse> getApplicationsByEmployerAndStatus(Long employerId, String status, int page, int size);

    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);

    ApplicationResponse getApplicationById(Long applicationId);

    boolean hasApplied(Long jobId, Long userId);

    long getApplicationCountByJob(Long jobId);

    long getApplicationCountByEmployer(Long employerId);

    long getActiveApplicationCountBySeeker(Long seekerId);
}

