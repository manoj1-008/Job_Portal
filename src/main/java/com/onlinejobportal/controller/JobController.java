package com.onlinejobportal.controller;

import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.JobSearchRequest;
import com.onlinejobportal.service.JobService;
import com.onlinejobportal.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;

    @GetMapping
    public String listJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<JobResponse> jobPage = jobService.getAllActiveJobs(page, size);

        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalJobs", jobPage.getTotalElements());
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());

        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });

        model.addAttribute("searchRequest", JobSearchRequest.defaultRequest());
        model.addAttribute("employmentTypes", new String[]{
                "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
        });
        model.addAttribute("experienceLevels", new String[]{
                "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
        });

        return "jobs/browse";
    }

    @GetMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) Double salaryMin,
            @RequestParam(required = false) Double salaryMax,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        JobSearchRequest searchRequest = new JobSearchRequest(
                keyword, location, employmentType, experienceLevel,
                salaryMin, salaryMax, sortBy, sortDirection, page, size);

        Long userId = AuthUtil.getCurrentUserId().orElse(null);
        Page<JobResponse> jobPage = (userId != null)
                ? jobService.searchJobs(searchRequest, userId)
                : jobService.searchJobs(searchRequest);

        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalJobs", jobPage.getTotalElements());
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());
        model.addAttribute("searchRequest", searchRequest);

        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });

        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("employmentType", employmentType);
        model.addAttribute("experienceLevel", experienceLevel);
        model.addAttribute("salaryMin", salaryMin);
        model.addAttribute("salaryMax", salaryMax);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("employmentTypes", new String[]{
                "FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP", "REMOTE", "FREELANCE"
        });
        model.addAttribute("experienceLevels", new String[]{
                "ENTRY", "JUNIOR", "MID", "SENIOR", "LEAD", "EXECUTIVE"
        });

        return "jobs/search-results";
    }

    @GetMapping("/view/{id}")
    public String viewJobDetails(@PathVariable Long id, Model model) {
        try {
            Long userId = AuthUtil.getCurrentUserId().orElse(null);
            JobResponse job = (userId != null)
                    ? jobService.getJobById(id, userId)
                    : jobService.getJobById(id);

            model.addAttribute("job", job);
            model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());

            AuthUtil.getCurrentUser().ifPresent(user -> {
                model.addAttribute("currentUser", user);
            });
        } catch (Exception e) {
            log.error("Error loading job details for ID {}: {}", id, e.getMessage());
            model.addAttribute("job", null);
            model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());
            AuthUtil.getCurrentUser().ifPresent(user -> {
                model.addAttribute("currentUser", user);
            });
        }

        return "jobs/detail";
    }

}
