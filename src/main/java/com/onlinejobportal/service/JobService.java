package com.onlinejobportal.service;

import com.onlinejobportal.dto.job.CreateJobRequest;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.JobSearchRequest;
import com.onlinejobportal.dto.job.UpdateJobRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobService {

    JobResponse createJob(Long employerId, CreateJobRequest request);

    JobResponse updateJob(Long jobId, Long employerId, UpdateJobRequest request);

    void deleteJob(Long jobId, Long employerId);

    JobResponse getJobById(Long jobId);

    JobResponse getJobById(Long jobId, Long userId);

    Page<JobResponse> getAllActiveJobs(int page, int size);

    Page<JobResponse> searchJobs(JobSearchRequest searchRequest);

    Page<JobResponse> searchJobs(JobSearchRequest searchRequest, Long userId);

    Page<JobResponse> getJobsByEmployer(Long employerId, int page, int size);

    List<JobResponse> getAllEmployerJobs(Long employerId);

    void toggleJobActive(Long jobId, Long employerId);

    void adminToggleJobActive(Long jobId);

    long getTotalActiveJobsCount();

    long getJobsCountByEmployer(Long employerId);

    long getNewJobsToday();
}

