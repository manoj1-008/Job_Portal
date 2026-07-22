package com.onlinejobportal.service;

import com.onlinejobportal.dto.resume.ResumeResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    ResumeResponse uploadResume(Long userId, MultipartFile file);

    Resource downloadResume(Long userId);

    ResumeResponse getResumeInfo(Long userId);

    void deleteResume(Long userId);

    boolean hasResume(Long userId);
}

