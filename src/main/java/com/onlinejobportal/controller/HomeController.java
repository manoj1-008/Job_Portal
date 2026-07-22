package com.onlinejobportal.controller;

import com.onlinejobportal.dto.job.JobResponse;
import com.onlinejobportal.dto.job.JobSearchRequest;
import com.onlinejobportal.service.JobService;
import com.onlinejobportal.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;

    @GetMapping("/")
    public String landingPage(Model model) {
        Page<JobResponse> recentJobs = jobService.getAllActiveJobs(0, 6);
        long totalJobs = jobService.getTotalActiveJobsCount();

        model.addAttribute("recentJobs", recentJobs.getContent());
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());

        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });

        return "index";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());
        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });
        return "about";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());
        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });
        return "contact";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (AuthUtil.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("isAuthenticated", false);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (AuthUtil.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("isAuthenticated", false);
        return "register";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("isAuthenticated", AuthUtil.isAuthenticated());
        AuthUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });
        return "access-denied";
    }

    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("title", "Error");
        model.addAttribute("message", "An unexpected error occurred.");
        return "error/error";
    }

}

