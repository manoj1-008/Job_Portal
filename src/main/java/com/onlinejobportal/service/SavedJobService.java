package com.onlinejobportal.service;

import com.onlinejobportal.dto.job.JobResponse;

import java.util.List;

public interface SavedJobService {

    void saveJob(Long userId, Long jobId);

    void unsaveJob(Long userId, Long jobId);

    List<JobResponse> getSavedJobs(Long userId);

    boolean isJobSaved(Long userId, Long jobId);

    long getSavedJobsCount(Long userId);
}

