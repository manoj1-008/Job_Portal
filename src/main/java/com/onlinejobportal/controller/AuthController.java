package com.onlinejobportal.controller;

import com.onlinejobportal.dto.auth.RegisterRequest;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/student/register")
    public String showStudentRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest(null, null, null, null, "ROLE_JOBSEEKER"));
        model.addAttribute("role", "ROLE_JOBSEEKER");
        model.addAttribute("isAuthenticated", false);
        return "auth/student-register";
    }

    @GetMapping("/employer/register")
    public String showEmployerRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest(null, null, null, null, "ROLE_EMPLOYER"));
        model.addAttribute("role", "ROLE_EMPLOYER");
        model.addAttribute("isAuthenticated", false);
        return "auth/employer-register";
    }

    @PostMapping("/student/register")
    public String registerStudent(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("role", "ROLE_JOBSEEKER");
            model.addAttribute("isAuthenticated", false);
            return "auth/student-register";
        }

        try {
            if (userService.existsByEmail(request.email())) {
                model.addAttribute("role", "ROLE_JOBSEEKER");
                model.addAttribute("isAuthenticated", false);
                model.addAttribute("emailError", "Email is already registered");
                return "auth/student-register";
            }

            User registeredUser = userService.registerStudent(request);
            log.info("New student registered: {}", registeredUser.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your email and password.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Student registration failed: {}", e.getMessage());
            model.addAttribute("role", "ROLE_JOBSEEKER");
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "auth/student-register";
        }
    }

    @PostMapping("/employer/register")
    public String registerEmployer(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("role", "ROLE_EMPLOYER");
            model.addAttribute("isAuthenticated", false);
            return "auth/employer-register";
        }

        try {
            if (userService.existsByEmail(request.email())) {
                model.addAttribute("role", "ROLE_EMPLOYER");
                model.addAttribute("isAuthenticated", false);
                model.addAttribute("emailError", "Email is already registered");
                return "auth/employer-register";
            }

            User registeredUser = userService.registerEmployer(request);
            log.info("New employer registered: {}", registeredUser.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your email and password.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Employer registration failed: {}", e.getMessage());
            model.addAttribute("role", "ROLE_EMPLOYER");
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "auth/employer-register";
        }
    }

}
