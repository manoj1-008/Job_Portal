package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.resume.ResumeResponse;
import com.onlinejobportal.entity.Resume;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.FileStorageException;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.repository.ResumeRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    @Override
    public ResumeResponse uploadResume(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileStorageException("Only PDF and DOC/DOCX files are allowed");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String storedFileName = UUID.randomUUID() + fileExtension;

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Delete existing resume if any
            resumeRepository.findByUser_Id(userId).ifPresent(existingResume -> {
                try {
                    Files.deleteIfExists(Paths.get(existingResume.getFilePath()));
                } catch (IOException e) {
                    log.warn("Could not delete old resume file: {}", existingResume.getFilePath());
                }
                resumeRepository.delete(existingResume);
            });

            // Save resume metadata
            Resume resume = Resume.builder()
                    .fileName(originalFileName != null ? originalFileName : "resume" + fileExtension)
                    .filePath(targetLocation.toString())
                    .fileType(contentType)
                    .user(user)
                    .build();

            Resume savedResume = resumeRepository.save(resume);
            log.info("Resume uploaded for user ID: {}", userId);

            return new ResumeResponse(
                    savedResume.getId(),
                    savedResume.getFileName(),
                    savedResume.getFileType(),
                    file.getSize(),
                    savedResume.getUploadedAt(),
                    "/api/resumes/download"
            );

        } catch (IOException e) {
            throw new FileStorageException("Could not store file. Please try again.", e);
        }
    }

    @Override
    public Resource downloadResume(Long userId) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "userId", userId));

        try {
            Path filePath = Paths.get(resume.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Could not read the resume file");
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Error accessing resume file", e);
        }
    }

    @Override
    public ResumeResponse getResumeInfo(Long userId) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "userId", userId));

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                0, // file size not stored in entity
                resume.getUploadedAt(),
                "/api/resumes/download"
        );
    }

    @Override
    @Transactional
    public void deleteResume(Long userId) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "userId", userId));

        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException e) {
            log.warn("Could not delete resume file: {}", resume.getFilePath());
        }

        resumeRepository.delete(resume);
        log.info("Resume deleted for user ID: {}", userId);
    }

    @Override
    public boolean hasResume(Long userId) {
        return resumeRepository.existsByUser_Id(userId);
    }
}

