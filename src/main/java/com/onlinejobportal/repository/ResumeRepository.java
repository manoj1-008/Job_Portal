package com.onlinejobportal.repository;

import com.onlinejobportal.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
}

