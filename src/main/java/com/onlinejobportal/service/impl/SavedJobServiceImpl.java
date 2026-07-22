package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.entity.Job;
import com.onlinejobportal.entity.SavedJob;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.DuplicateResourceException;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.SavedJobRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public void saveJob(Long userId, Long jobId) {
        if (savedJobRepository.existsByUser_IdAndJob_Id(userId, jobId)) {
            throw new DuplicateResourceException("Job is already saved");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .job(job)
                .build();

        savedJobRepository.save(savedJob);
        log.info("Job ID: {} saved by User ID: {}", jobId, userId);
    }

    @Override
    public void unsaveJob(Long userId, Long jobId) {
        if (!savedJobRepository.existsByUser_IdAndJob_Id(userId, jobId)) {
            throw new ResourceNotFoundException("SavedJob", "userAndJob", userId + "-" + jobId);
        }
        savedJobRepository.deleteByUser_IdAndJob_Id(userId, jobId);
        log.info("Job ID: {} unsaved by User ID: {}", jobId, userId);
    }

    @Override
    public List<JobResponse> getSavedJobs(Long userId) {
        return savedJobRepository.findByUser_Id(userId)
                .stream()
                .map(sj -> mapToJobResponse(sj.getJob(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isJobSaved(Long userId, Long jobId) {
        return savedJobRepository.existsByUser_IdAndJob_Id(userId, jobId);
    }

    @Override
    public long getSavedJobsCount(Long userId) {
        return savedJobRepository.countByUser_Id(userId);
    }

    private JobResponse mapToJobResponse(Job job, Long userId) {
        boolean isExpired = job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now());

        String salaryRange = null;
        if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
            salaryRange = String.format("$%.0f - $%.0f", job.getSalaryMin(), job.getSalaryMax());
        } else if (job.getSalaryMin() != null) {
            salaryRange = String.format("From $%.0f", job.getSalaryMin());
        } else if (job.getSalaryMax() != null) {
            salaryRange = String.format("Up to $%.0f", job.getSalaryMax());
        }

        int applicationCount = job.getApplications() != null ? job.getApplications().size() : 0;

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
                !job.isActive() || isExpired,
                job.getEmployer().getId(),
                job.getEmployer().getFullName(),
                job.getEmployer().getEmail(),
                applicationCount,
                false,
                true
        );
    }
}

