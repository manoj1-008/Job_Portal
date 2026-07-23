package com.onlinejobportal.repository;

import com.onlinejobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();

    Optional<Job> findByTitleAndCompany(String title, String company);

    boolean existsByTitleAndCompany(String title, String company);

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
