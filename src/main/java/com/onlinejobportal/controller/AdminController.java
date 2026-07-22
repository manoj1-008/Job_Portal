package com.onlinejobportal.controller;

import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;
import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.service.DashboardService;
import com.onlinejobportal.service.JobService;
import com.onlinejobportal.service.UserService;
import com.onlinejobportal.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final JobService jobService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsResponse stats = dashboardService.getAdminDashboardStats();

        // Get recent users (last 10)
        Page<User> recentUsers = userService.getAllUsers(0, 10);
        model.addAttribute("recentUsers", recentUsers.getContent());

        // Get recent jobs (last 10)
        Page<JobResponse> recentJobs = jobService.getAllActiveJobs(0, 10);
        model.addAttribute("recentJobs", recentJobs.getContent());

        model.addAttribute("stats", stats);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<User> userPage;
        if (keyword != null && !keyword.isBlank() && role != null && !role.isBlank()) {
            userPage = userService.searchUsersByRole(role, keyword, page, size);
        } else if (keyword != null && !keyword.isBlank()) {
            userPage = userService.searchUsers(keyword, page, size);
        } else if (role != null && !role.isBlank()) {
            userPage = userService.getUsersByRole(role, page, size);
        } else {
            userPage = userService.getAllUsers(page, size);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalUsers", userPage.getTotalElements());
        model.addAttribute("selectedRole", role);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

// Role distribution counts - match DB role names (ROLE_ADMIN, ROLE_EMPLOYER, ROLE_JOBSEEKER)
        model.addAttribute("totalAdmins", userService.getUsersCountByRole("ROLE_ADMIN"));
        model.addAttribute("totalEmployers", userService.getUsersCountByRole("ROLE_EMPLOYER"));
        model.addAttribute("totalJobSeekers", userService.getUsersCountByRole("ROLE_JOBSEEKER"));

        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            boolean wasEnabled = user.isEnabled();
            userService.toggleUserEnabled(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + user.getEmail() + " " + (wasEnabled ? "disabled" : "enabled") + " successfully.");
            log.info("Admin toggled user {} enabled status to {}", id, !wasEnabled);
        } catch (Exception e) {
            log.error("Failed to toggle user {} status: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            String userEmail = user.getEmail();
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + userEmail + " deleted successfully.");
            log.info("Admin deleted user: {} ({})", userEmail, id);
        } catch (Exception e) {
            log.error("Failed to delete user {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String manageJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<JobResponse> jobPage = jobService.getAllActiveJobs(page, size);

        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalJobs", jobPage.getTotalElements());
        model.addAttribute("isAuthenticated", true);
        AuthUtil.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));

        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJob(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            JobResponse job = jobService.getJobById(id);
            jobService.adminToggleJobActive(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Job '" + job.title() + "' " + (job.active() ? "deactivated" : "activated") + ".");
            log.info("Admin toggled job {} active status", id);
        } catch (Exception e) {
            log.error("Failed to toggle job {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to toggle job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            JobResponse job = jobService.getJobById(id);
            jobService.adminToggleJobActive(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Job '" + job.title() + "' has been deactivated by admin.");
            log.info("Admin deactivated job {}: {}", id, job.title());
        } catch (Exception e) {
            log.error("Failed to delete job {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

}

