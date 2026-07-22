package com.onlinejobportal.controller;

import com.onlinejobportal.dto.application.ApplicationResponse;
import com.onlinejobportal.dto.application.ApplyJobRequest;
import com.onlinejobportal.dto.auth.ChangePasswordRequest;
import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.resume.ResumeResponse;
import com.onlinejobportal.dto.student.StudentProfileRequest;
import com.onlinejobportal.dto.student.StudentProfileResponse;
import com.onlinejobportal.service.DashboardService;
import com.onlinejobportal.service.JobApplicationService;
import com.onlinejobportal.service.JobService;
import com.onlinejobportal.service.ResumeService;
import com.onlinejobportal.service.SavedJobService;
import com.onlinejobportal.service.UserService;
import com.onlinejobportal.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final DashboardService dashboardService;
    private final JobApplicationService applicationService;
    private final SavedJobService savedJobService;
    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        DashboardStatsResponse stats = dashboardService.getJobSeekerDashboardStats(userId);

        model.addAttribute("stats", stats);
        model.addAttribute("profileCompletion", "60%");
        model.addAttribute("hasResume", resumeService.hasResume(userId));

        List<JobResponse> savedJobs = savedJobService.getSavedJobs(userId);
        model.addAttribute("savedJobs", savedJobs.stream().limit(5).toList());

        Page<ApplicationResponse> recentApplications = applicationService
                .getApplicationsByJobSeeker(userId, 0, 5);
        model.addAttribute("recentApplications", recentApplications.getContent());

        List<JobResponse> recommended = jobService.getAllActiveJobs(0, 5).getContent();
        model.addAttribute("recommendedJobs", recommended);

        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "student/dashboard";
    }

    @GetMapping("/applications")
    public String myApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        Page<ApplicationResponse> applications = applicationService
                .getApplicationsByJobSeeker(userId, page, size);

        model.addAttribute("applications", applications.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", applications.getTotalPages());
        model.addAttribute("totalApplications", applications.getTotalElements());
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "student/applications";
    }

    @PostMapping("/jobs/apply")
    public String applyForJob(@RequestParam Long jobId,
                               @RequestParam(required = false) String coverLetter,
                               RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            ApplyJobRequest request = new ApplyJobRequest(jobId, coverLetter);
            applicationService.applyForJob(userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            log.info("Student {} applied for job {}", userId, jobId);
        } catch (Exception e) {
            log.error("Failed to apply for job {}: {}", jobId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to apply: " + e.getMessage());
        }

        return "redirect:/jobs/view/" + jobId;
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdrawApplication(@PathVariable Long id,
                                       RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            applicationService.withdrawApplication(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Application withdrawn successfully.");
            log.info("Student {} withdrew application {}", userId, id);
        } catch (Exception e) {
            log.error("Failed to withdraw application {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to withdraw: " + e.getMessage());
        }

        return "redirect:/student/applications";
    }

    @GetMapping("/saved-jobs")
    public String savedJobs(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        List<JobResponse> jobs = savedJobService.getSavedJobs(userId);

        model.addAttribute("savedJobs", jobs);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "student/saved-jobs";
    }

    @PostMapping("/jobs/save/{jobId}")
    public String saveJob(@PathVariable Long jobId,
                           RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            savedJobService.saveJob(userId, jobId);
            redirectAttributes.addFlashAttribute("successMessage", "Job saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save job: " + e.getMessage());
        }

        return "redirect:/jobs/view/" + jobId;
    }

    @PostMapping("/jobs/unsave/{jobId}")
    public String unsaveJob(@PathVariable Long jobId,
                             RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            savedJobService.unsaveJob(userId, jobId);
            redirectAttributes.addFlashAttribute("successMessage", "Job removed from saved jobs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unsave job: " + e.getMessage());
        }

        return "redirect:/student/saved-jobs";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        StudentProfileResponse profile = userService.getStudentProfile(userId);

        model.addAttribute("profile", profile);
        model.addAttribute("profileRequest", new StudentProfileRequest(
                profile.fullName(), profile.phone(), null, null, null, null, null, null, null
        ));
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("hasResume", resumeService.hasResume(userId));
        try {
            ResumeResponse resumeInfo = resumeService.getResumeInfo(userId);
            model.addAttribute("resumeFileName", resumeInfo.fileName());
            model.addAttribute("resumeFileSize", resumeInfo.fileSize() + " bytes");
        } catch (Exception e) {
            model.addAttribute("resumeFileName", null);
            model.addAttribute("resumeFileSize", null);
        }
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileRequest") StudentProfileRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("hasResume", resumeService.hasResume(userId));
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
            return "student/profile";
        }

        try {
            userService.updateStudentProfile(userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            log.info("Student {} updated profile", userId);
        } catch (Exception e) {
            log.error("Failed to update profile for {}: {}", userId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        return "redirect:/student/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request,
                                  RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            userService.changePassword(userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            log.info("Student {} changed password", userId);
        } catch (Exception e) {
            log.error("Failed to change password for {}: {}", userId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }

        return "redirect:/student/profile";
    }

    @PostMapping("/resume/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/student/profile";
        }

        try {
            ResumeResponse resume = resumeService.uploadResume(userId, file);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Resume uploaded successfully: " + resume.fileName());
            log.info("Student {} uploaded resume: {}", userId, resume.fileName());
        } catch (Exception e) {
            log.error("Failed to upload resume for {}: {}", userId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload resume: " + e.getMessage());
        }

        return "redirect:/student/profile";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("notifications", List.of());
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "student/notifications";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest("", "", ""));
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "student/settings";
    }

    @GetMapping("/resume")
    public String resumePage(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("isAuthenticated", true);
        boolean hasResume = resumeService.hasResume(userId);
        model.addAttribute("hasResume", hasResume);
        if (hasResume) {
            try {
                ResumeResponse resumeInfo = resumeService.getResumeInfo(userId);
                model.addAttribute("resumeFileName", resumeInfo.fileName());
                model.addAttribute("resumeFileSize", resumeInfo.fileSize() + " bytes");
            } catch (Exception e) {
                model.addAttribute("resumeFileName", null);
                model.addAttribute("resumeFileSize", null);
            }
        } else {
            model.addAttribute("resumeFileName", null);
            model.addAttribute("resumeFileSize", null);
        }
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "student/resume-upload";
    }

    @GetMapping("/resume/download")
    public ResponseEntity<Resource> downloadResume() {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            Resource resource = resumeService.downloadResume(userId);
            ResumeResponse resumeInfo = resumeService.getResumeInfo(userId);

            String contentType = resumeInfo.fileType() != null ? resumeInfo.fileType() : "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resumeInfo.fileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Failed to download resume for {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/resume/delete")
    public String deleteResume(RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            resumeService.deleteResume(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Resume deleted successfully.");
            log.info("Student {} deleted resume", userId);
        } catch (Exception e) {
            log.error("Failed to delete resume for {}: {}", userId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete resume: " + e.getMessage());
        }

        return "redirect:/student/profile";
    }

}
