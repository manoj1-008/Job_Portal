package com.onlinejobportal.controller;

import com.onlinejobportal.dto.application.ApplicationResponse;
import com.onlinejobportal.dto.application.ApplicationStatusUpdateRequest;
import com.onlinejobportal.dto.employer.EmployerProfileRequest;
import com.onlinejobportal.dto.employer.EmployerProfileResponse;
import com.onlinejobportal.dto.job.CreateJobRequest;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.UpdateJobRequest;
import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;
import com.onlinejobportal.service.DashboardService;
import com.onlinejobportal.service.JobApplicationService;
import com.onlinejobportal.service.JobService;
import com.onlinejobportal.service.UserService;
import com.onlinejobportal.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
@Slf4j
public class EmployerController {

    private final JobService jobService;
    private final JobApplicationService applicationService;
    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Long userId = AuthUtil.getCurrentUserIdOrThrow();

            DashboardStatsResponse stats = dashboardService.getEmployerDashboardStats(userId);
            model.addAttribute("stats", stats);

            List<JobResponse> recentJobs = jobService.getAllEmployerJobs(userId);
            model.addAttribute("recentJobs", recentJobs != null ? recentJobs.stream().limit(5).toList() : java.util.Collections.emptyList());
            model.addAttribute("totalJobs", recentJobs != null ? recentJobs.size() : 0);

            model.addAttribute("isAuthenticated", true);
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        } catch (Exception e) {
            log.error("Error loading employer dashboard: {}", e.getMessage(), e);
            model.addAttribute("stats", DashboardStatsResponse.empty());
            model.addAttribute("recentJobs", java.util.Collections.emptyList());
            model.addAttribute("totalJobs", 0);
            model.addAttribute("isAuthenticated", true);
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "employer/dashboard";
    }

    @GetMapping("/jobs")
    public String myJobs(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        List<JobResponse> jobs = jobService.getAllEmployerJobs(userId);

        model.addAttribute("jobs", jobs);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "employer/jobs";
    }

    @GetMapping("/jobs/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("createJobRequest", new CreateJobRequest(
                null, null, null, null, null, null, null, null, null, null
        ));
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        model.addAttribute("employmentTypes", new String[]{
                "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
        });
        model.addAttribute("experienceLevels", new String[]{
                "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
        });

        return "employer/post-job";
    }

    @PostMapping("/jobs/post")
    public String postJob(@Valid @ModelAttribute("createJobRequest") CreateJobRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("isAuthenticated", true);
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
            model.addAttribute("employmentTypes", new String[]{
                    "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
            });
            model.addAttribute("experienceLevels", new String[]{
                    "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
            });
            return "employer/post-job";
        }

        try {
            JobResponse job = jobService.createJob(userId, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Job posted successfully: " + job.title());
            log.info("Employer {} posted job: {}", userId, job.title());
            return "redirect:/employer/jobs";
        } catch (Exception e) {
            log.error("Failed to post job for employer {}: {}", userId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to post job: " + e.getMessage());
            return "redirect:/employer/jobs/post";
        }
    }

    @GetMapping("/jobs/edit/{id}")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        JobResponse job = jobService.getJobById(id, userId);

        model.addAttribute("updateJobRequest", new UpdateJobRequest(
                job.title(), job.description(), job.company(), job.location(),
                job.employmentType(), job.experienceLevel(), job.salaryMin(), job.salaryMax(),
                job.skills(), job.deadline(), job.active()
        ));
        model.addAttribute("jobId", id);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        model.addAttribute("employmentTypes", new String[]{
                "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
        });
        model.addAttribute("experienceLevels", new String[]{
                "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
        });

        return "employer/edit-job";
    }

    @PostMapping("/jobs/edit/{id}")
    public String updateJob(@PathVariable Long id,
                             @Valid @ModelAttribute("updateJobRequest") UpdateJobRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            model.addAttribute("isAuthenticated", true);
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
            model.addAttribute("employmentTypes", new String[]{
                    "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
            });
            model.addAttribute("experienceLevels", new String[]{
                    "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
            });
            return "employer/edit-job";
        }

        try {
            jobService.updateJob(id, userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
            log.info("Employer {} updated job {}", userId, id);
            return "redirect:/employer/jobs";
        } catch (Exception e) {
            log.error("Failed to update job {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update job: " + e.getMessage());
            return "redirect:/employer/jobs/edit/" + id;
        }
    }

    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            jobService.deleteJob(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
            log.info("Employer {} deleted job {}", userId, id);
        } catch (Exception e) {
            log.error("Failed to delete job {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete job: " + e.getMessage());
        }

        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/toggle/{id}")
    public String toggleJobActive(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        try {
            jobService.toggleJobActive(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Job status updated.");
            log.info("Employer {} toggled job {} active status", userId, id);
        } catch (Exception e) {
            log.error("Failed to toggle job {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update job status: " + e.getMessage());
        }

        return "redirect:/employer/jobs";
    }

    @GetMapping("/applicants")
    public String viewApplicants(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();

        Page<ApplicationResponse> applications;
        if (status != null && !status.isEmpty()) {
            applications = applicationService.getApplicationsByEmployerAndStatus(userId, status, page, size);
        } else {
            applications = applicationService.getApplicationsByEmployer(userId, page, size);
        }

        model.addAttribute("applications", applications.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", applications.getTotalPages());
        model.addAttribute("totalApplications", applications.getTotalElements());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("jobId", jobId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        model.addAttribute("statuses", new String[]{
                "PENDING", "REVIEWING", "SHORTLISTED", "REJECTED", "HIRED"
        });

        // Add employer jobs for filter dropdown
        List<JobResponse> employerJobs = jobService.getAllEmployerJobs(userId);
        model.addAttribute("employerJobs", employerJobs);

        return "employer/applicants";
    }

    @PostMapping("/applicants/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                           @RequestParam String status,
                                           RedirectAttributes redirectAttributes) {
        try {
            ApplicationStatusUpdateRequest request = new ApplicationStatusUpdateRequest(status);
            applicationService.updateApplicationStatus(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Application status updated to " + status);
            log.info("Application {} status updated to {}", id, status);
        } catch (Exception e) {
            log.error("Failed to update application {} status: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + e.getMessage());
        }

        return "redirect:/employer/applicants";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        EmployerProfileResponse profile = userService.getEmployerProfile(userId);

        model.addAttribute("profile", profile);
        model.addAttribute("companyName", profile.companyName());
        model.addAttribute("companyDescription", profile.companyDescription());
        model.addAttribute("companyWebsite", profile.companyWebsite());
        model.addAttribute("companyLocation", profile.companyLocation());
        model.addAttribute("companyIndustry", null);
        model.addAttribute("companySize", null);
        model.addAttribute("companyFounded", null);
        model.addAttribute("benefits", null);
        model.addAttribute("openPositions", jobService.getAllEmployerJobs(userId));
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "employer/company-profile";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("analytics", dashboardService.getEmployerDashboardStats(userId));
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "employer/analytics";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("notifications", null);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "employer/notifications";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        Long userId = AuthUtil.getCurrentUserIdOrThrow();
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        return "employer/settings";
    }

    @GetMapping("/applicants/{id}")
    public String viewCandidateProfile(@PathVariable Long id, Model model) {
        try {
            ApplicationResponse app = applicationService.getApplicationById(id);
            model.addAttribute("candidate", userService.getUserById(app.jobSeekerId()));
            model.addAttribute("applicationId", app.id());
            model.addAttribute("applicationStatus", app.status());
            model.addAttribute("jobTitle", app.jobTitle());
            model.addAttribute("appliedAt", app.appliedAt());
            model.addAttribute("coverLetter", app.coverLetter());
            model.addAttribute("skills", null);
            model.addAttribute("experiences", null);
            model.addAttribute("hasResume", app.hasResume());
            model.addAttribute("resumeFileName", null);
            model.addAttribute("isAuthenticated", true);
            AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        } catch (Exception e) {
            model.addAttribute("candidate", null);
        }
        return "employer/candidate-profile";
    }

}
