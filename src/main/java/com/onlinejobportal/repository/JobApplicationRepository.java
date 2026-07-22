package com.onlinejobportal.repository;

import com.onlinejobportal.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByJobSeeker_Id(Long jobSeekerId);

    Page<JobApplication> findByJobSeeker_Id(Long jobSeekerId, Pageable pageable);

    List<JobApplication> findByJob_Id(Long jobId);

    Page<JobApplication> findByJob_Id(Long jobId, Pageable pageable);

    boolean existsByJob_IdAndJobSeeker_Id(Long jobId, Long jobSeekerId);

    long countByStatus(JobApplication.ApplicationStatus status);

    long countByJobSeeker_Id(Long jobSeekerId);

    long countByJob_Id(Long jobId);

    long countByJob_Employer_Id(Long employerId);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.status = :status AND ja.job.employer.id = :employerId")
    long countByStatusAndEmployerId(@Param("status") JobApplication.ApplicationStatus status,
                                     @Param("employerId") Long employerId);

    List<JobApplication> findByJob_IdAndStatus(Long jobId, JobApplication.ApplicationStatus status);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.id = :jobId ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobIdOrderByAppliedAtDesc(@Param("jobId") Long jobId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.employer.id = :employerId ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findByEmployerId(@Param("employerId") Long employerId, Pageable pageable);

    @Query("SELECT ja FROM JobApplication ja WHERE " +
           "ja.job.employer.id = :employerId AND " +
           "(:status IS NULL OR ja.status = :status) " +
           "ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findByEmployerIdAndStatus(@Param("employerId") Long employerId,
                                                     @Param("status") JobApplication.ApplicationStatus status,
                                                     Pageable pageable);

    Optional<JobApplication> findByJob_IdAndJobSeeker_Id(Long jobId, Long jobSeekerId);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.jobSeeker.id = :seekerId AND ja.status NOT IN ('REJECTED', 'HIRED')")
    long countActiveApplicationsBySeekerId(@Param("seekerId") Long seekerId);
}

