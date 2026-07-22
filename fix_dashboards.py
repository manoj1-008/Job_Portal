import os

# ========= 1. FIX EMPLOYER CONTROLLER =========
ctrl_path = 'src/main/java/com/onlinejobportal/controller/EmployerController.java'
with open(ctrl_path, 'r') as f:
    content = f.read()

# Add import if missing
if 'import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;' not in content:
    # Find an import line to add after
    idx = content.find('import com.onlinejobportal.dto.')
    if idx > 0:
        line_end = content.index('\n', idx)
        content = content[:line_end+1] + 'import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;\n' + content[line_end+1:]

# Replace dashboard method
old_marker = '@GetMapping("/dashboard")'
idx = content.find(old_marker)
if idx > 0:
    # Find the end of the method
    method_start = content.rindex('\n', 0, idx) + 1
    # Find the next @GetMapping after this method
    next_marker = content.find('@GetMapping', idx + 1)
    if next_marker > 0:
        # Go back to find the return statement and closing brace
        method_end = content.rindex('\n    }\n', idx, next_marker) + 1
        method_end = content.index('\n    }\n', method_end) + 6
    
    new_method = '''    @GetMapping("/dashboard")
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
    }'''
    
    # Find the old method - from method_start to method_end
    old_method = content[method_start:method_end]
    content = content.replace(old_method, new_method, 1)
    with open(ctrl_path, 'w') as f:
        f.write(content)
    print('✓ EmployerController.java fixed')
else:
    print('✗ Could not find dashboard method')

# ========= 2. FIX DASHBOARD TEMPLATE HTML =========
html_path = 'src/main/resources/templates/employer/dashboard.html'
with open(html_path, 'r') as f:
    html = f.read()

# Count tag balances before
o_div = html.count('<div')
c_div = html.count('</div>')
o_span = html.count('<span')
c_span = html.count('</span>')
print(f'Before fix: <div>: {o_div} open, {c_div} close (diff={o_div-c_div}), <span>: {o_span} open, {c_span} close (diff={o_span-c_span})')

# The HTML is severely broken. Rewrite it properly.
new_html = '''<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      lang="en">
<head th:replace="~{fragments/header::head('Employer Dashboard - JobPortal', 'Your recruitment dashboard')}"></head>
<body>
<div th:replace="~{fragments/employer-sidebar::sidebar('Dashboard', 'dashboard')}"></div>
<div class="dashboard-main">
    <div class="container-fluid">
        <!-- Welcome Section -->
        <div class="glass-card-lg p-5 mb-5 fade-in">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <div class="d-flex align-items-center gap-4 mb-3">
                        <div class="profile-avatar" style="width: 72px; height: 72px; font-size: 2rem;">
                            <span th:text="${currentUser != null ? #strings.substring(currentUser.fullName, 0, 1) : 'E'}">E</span>
                        </div>
                        <div>
                            <h2 class="fw-bold mb-1" style="font-size: 1.8rem;">Welcome back, <span th:text="${currentUser?.fullName ?: 'Employer'}">Employer</span></h2>
                            <p style="color: var(--text-secondary);">Your recruitment hub. Manage jobs, review applicants, and find top talent.</p>
                        </div>
                    <div class="d-flex flex-wrap gap-3 mt-4">
                        <a th:href="@{/employer/jobs/post}" class="btn btn-primary-gradient"><i class="fas fa-plus"></i> Post New Job</a>
                        <a th:href="@{/employer/applicants}" class="btn btn-secondary-glass"><i class="fas fa-users"></i> View Applicants</a>
                    </div>
                <div class="col-lg-4 d-none d-lg-block">
                    <div class="glass p-4 text-center">
                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">Jobs Posted</div>
                        <div style="font-size: 2.5rem; font-weight: 800; color: var(--color-primary);" th:text="${stats?.employerJobCount ?: 0}">0</div>
                </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-4 mb-5">
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon primary"><i class="fas fa-briefcase"></i></div>
                    <div class="stats-number count-up" th:attr="data-target=${stats != null ? stats.employerJobCount : 0}" th:text="${stats?.employerJobCount ?: 0}">0</div>
                    <div class="stats-label">Jobs Posted</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon success"><i class="fas fa-check-circle"></i></div>
                    <div class="stats-number count-up" th:attr="data-target=${stats != null ? stats.employerActiveJobs : 0}" th:text="${stats?.employerActiveJobs ?: 0}">0</div>
                    <div class="stats-label">Active Jobs</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon info"><i class="fas fa-users"></i></div>
                    <div class="stats-number count-up" th:attr="data-target=${stats != null ? stats.employerTotalApplications : 0}" th:text="${stats?.employerTotalApplications ?: 0}">0</div>
                    <div class="stats-label">Total Applicants</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon warning"><i class="fas fa-clock"></i></div>
                    <div class="stats-number count-up" th:attr="data-target=${stats != null ? stats.employerPendingApplications : 0}" th:text="${stats?.employerPendingApplications ?: 0}">0</div>
                    <div class="stats-label">Pending</div>
            </div>
            <div class="col-6 col-xl-2">
                <div class="stats-card card fade-in">
                    <div class="stats-icon pink"><i class="fas fa-search"></i></div>
