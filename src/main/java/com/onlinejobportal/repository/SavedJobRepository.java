package com.onlinejobportal.repository;

import com.onlinejobportal.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByUser_Id(Long userId);

    boolean existsByUser_IdAndJob_Id(Long userId, Long jobId);

    void deleteByUser_IdAndJob_Id(Long userId, Long jobId);

    Optional<SavedJob> findByUser_IdAndJob_Id(Long userId, Long jobId);

    long countByUser_Id(Long userId);
}

