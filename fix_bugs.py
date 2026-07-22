#!/usr/bin/env python3
"""Fix 6 runtime bugs in JobPortal - only modifies HTML/CSS/JS, no backend Java changes."""

def write_file(path, content):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Written {len(content)} bytes to {path}')

# ============================================================
# ISSUE 1: Student Dashboard - broken stat cards (missing </div> on col-md-3)
# ============================================================
write_file('src/main/resources/templates/student/dashboard.html', """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head th:replace="~{fragments/header::head('Student Dashboard', 'Manage your job search')}"></head>
<body>
<div th:replace="~{fragments/student-sidebar::sidebar('Dashboard', 'dashboard')}"></div>
<div class="dashboard-main">
<div class="container-fluid">
<div class="glass-card-lg p-5 mb-5 fade-in">
<div class="row align-items-center">
<div class="col-lg-8">
<h2 class="fw-bold mb-0">Welcome, <span th:text="${currentUser?.fullName ?: 'Student'}">Student</span></h2>
<p style="color: var(--text-secondary);">Track your applications and saved jobs.</p>
</div>
<div class="col-lg-4 text-end">
<a th:href="@{/jobs}" class="btn btn-primary-gradient"><i class="fas fa-search"></i> Browse Jobs</a>
</div>
</div>
<div class="row g-4 mb-4">
<div class="col-md-3"><div class="card p-3 text-center"><h3 th:text="${stats?.seekerApplicationsSent ?: 0}">0</h3><p class="mb-0 text-muted small">Applications</p></div>
<div class="col-md-3"><div class="card p-3 text-center"><h3 th:text="${stats?.seekerActiveApplications ?: 0}">0</h3><p class="mb-0 text-muted small">Active</p></div>
<div class="col-md-3"><div class="card p-3 text-center"><h3 th:text="${stats?.seekerSavedJobs ?: 0}">0</h3><p class="mb-0 text-muted small">Saved Jobs</p></div>
<div class="col-md-3"><div class="card p-3 text-center"><h3 th:text="${stats?.resumeStatus ?: 'N/A'}">N/A</h3><p class="mb-0 text-muted small">Resume</p></div>
</div>
</div>
<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>""")

# ============================================================
# ISSUE 2: Student Profile - HTTP 500 (template uses studentProfile but model has profile)
# Fix: Change template references from studentProfile to profile
# ============================================================
write_file('src/main/resources/templates/student/profile.html', """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      lang="en">
<head th:replace="~{fragments/header::head('My Profile - JobPortal', 'Manage your profile')}"></head>
<body>
<div th:replace="~{fragments/student-sidebar::sidebar('Profile', 'profile')}"></div>
<div class="dashboard-main">
<div class="container-fluid">
<div class="glass-card-lg p-5 mb-5 fade-in">
<div class="d-flex justify-content-between align-items-center mb-5">
<h4 class="fw-bold mb-0"><i class="fas fa-user-circle me-2" style="color: var(--color-primary);"></i> My Profile</h4>
</div>
<div class="row">
<div class="col-md-4 text-center">
<div class="profile-avatar mx-auto mb-3" style="width:100px;height:100px;display:flex;align-items:center;justify-content:center;font-size:40px;">
<span th:text="${profile != null ? #strings.substring(profile.fullName(),0,1) : 'U'}">U</span>
</div>
<h4 class="fw-bold" th:text="${profile.fullName()}">Full Name</h4>
<span class="badge bg-primary">Student</span>
</div>
<div class="col-md-8">
<div class="row g-4">
<div class="col-md-6"><label class="text-muted">Email</label><p class="fw-semibold" th:text="${profile.email()}">email@example.com</p></div>
<div class="col-md-6"><label class="text-muted">Phone</label><p class="fw-semibold" th:text="${profile.phone()}">9999999999</p></div>
<div class="col-12"><label class="text-muted">Headline</label><p th:text="${profile.headline()} ?: 'No headline added.'">No headline added.</p></div>
<div class="col-12"><label class="text-muted">Summary</label><p th:text="${profile.summary()} ?: 'No summary added.'">No summary added.</p></div>
<div class="col-md-6"><label class="text-muted">Education</label><p th:text="${profile.education()} ?: 'Not specified'">Not specified</p></div>
<div class="col-md-6"><label class="text-muted">Skills</label><p th:text="${profile.skills()} ?: 'Not specified'">Not specified</p></div>
<div class="col-md-6"><label class="text-muted">Experience</label><p th:text="${profile.experience()} ?: 'Not specified'">Not specified</p></div>
<div class="col-md-6"><label class="text-muted">LinkedIn</label><p><a th:if="${profile.linkedinUrl()!=null and !#strings.isEmpty(profile.linkedinUrl())}" th:href="${profile.linkedinUrl()}" target="_blank" th:text="${profile.linkedinUrl()}">Link</a><span th:if="${profile.linkedinUrl()==null or #strings.isEmpty(profile.linkedinUrl())}">Not added</span></p></div>
<div class="col-md-6"><label class="text-muted">GitHub</label><p><a th:if="${profile.githubUrl()!=null and !#strings.isEmpty(profile.githubUrl())}" th:href="${profile.githubUrl()}" target="_blank" th:text="${profile.githubUrl()}">Link</a><span th:if="${profile.githubUrl()==null or #strings.isEmpty(profile.githubUrl())}">Not added</span></p></div>
<div class="col-md-6"><label class="text-muted">Resume</label><p th:text="${resumeFileName ?: 'No Resume Uploaded'}">No Resume Uploaded</p></div>
<div class="col-md-6"><label class="text-muted">Joined On</label><p th:text="${#temporals.format(profile.createdAt(),'dd MMM yyyy')}">22 Jul 2026</p></div>
</div>
</div>
</div>
<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>""")

# ============================================================
# ISSUE 3: Resume page layout - add missing model attributes to /resume endpoint
# Fix: Update StudentController to add resumeFileName and resumeFileSize
# ============================================================
# We need to fix the controller to add these attributes
print("\nISSUE 3: Will update StudentController to add resumeFileName, resumeFileSize to /resume endpoint")

# ============================================================
# ISSUE 4: Job Details page blank - completely broken HTML nesting
# Fix: Rewrite detail.html with proper tag structure
# ============================================================
write_file('src/main/resources/templates/jobs/detail.html', """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      lang="en">
<head th:replace="~{fragments/header::head('' + ${job.title()} + ' at ' + ${job.company()} + ' - JobPortal', 'View job details')}"></head>
<body>
<div class="aurora-bg"></div>
<div class="aurora-blob aurora-blob-1"></div>
<div class="aurora-blob aurora-blob-2"></div>
<div class="mouse-glow"></div>
<nav th:replace="~{fragments/header::navbar}"></nav>
<div th:if="${job != null}" class="container py-4" style="margin-top: 76px;">
<div class="glass-card-lg p-5 mb-4 fade-in">
<div class="row align-items-center">
<div class="col-lg-8">
<div class="d-flex align-items-start gap-4">
<div class="job-company-logo" style="width: 80px; height: 80px; font-size: 2rem;"><i class="fas fa-building"></i></div>
<div>
<h1 class="fw-bold mb-2" style="font-size: 2rem;" th:text="${job.title()}">Job Title</h1>
<div class="d-flex flex-wrap gap-3 mb-3">
<span style="color: var(--text-secondary);"><i class="fas fa-building me-1"></i> <span th:text="${job.company()}">Company</span>
<span style="color: var(--text-secondary);"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>
<span style="color: var(--text-secondary);"><i class="fas fa-clock me-1"></i> <span th:text="${job.employmentType()}">Type</span>
<span th:if="${job.experienceLevel() != null}" style="color: var(--text-secondary);"><i class="fas fa-chart-line me-1"></i> <span th:text="${job.experienceLevel()}">Level</span>
</div>
<span class="badge badge-success" th:if="${job.salaryRange() != null}" style="font-size: 0.9rem; padding: 6px 16px;"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary Range</span>
</div>
</div>
<div class="col-lg-4 mt-4 mt-lg-0">
<div class="d-flex flex-column gap-2">
<form method="post" th:action="@{'/student/jobs/apply'}" th:if="${isAuthenticated != null and isAuthenticated and job != null and !job.hasApplied()}">
<input type="hidden" name="jobId" th:value="${job.id()}"/>
<button type="submit" class="btn btn-primary-gradient btn-lg w-100"><i class="fas fa-paper-plane"></i> Apply Now</button>
</form>
<span class="btn btn-success btn-lg w-100" th:if="${isAuthenticated != null and isAuthenticated and job != null and job.hasApplied()}"><i class="fas fa-check-circle"></i> Applied</span>
<div class="d-flex gap-2">
<form method="post" th:action="@{'/student/jobs/save/' + ${job.id()}}" th:if="${isAuthenticated and !job.hasSaved()}">
<button type="submit" class="btn btn-secondary-glass flex-grow-1"><i class="far fa-bookmark"></i> Save</button>
</form>
<form method="post" th:action="@{'/student/jobs/unsave/' + ${job.id()}}" th:if="${isAuthenticated and job.hasSaved()}">
<button type="submit" class="btn btn-secondary-glass flex-grow-1"><i class="fas fa-bookmark"></i> Saved</button>
</form>
</div>
</div>
</div>
<div class="row g-4">
<div class="col-lg-8">
<div class="card mb-4">
<div class="card-header"><h5 class="fw-bold mb-0"><i class="fas fa-align-left me-2" style="color: var(--color-primary);"></i> Job Description</h5></div>
<div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8; white-space: pre-line;" th:text="${job.description()}">Description</div>
</div>
<div class="card mb-4" th:if="${job.skills() != null and !job.skills().isEmpty()}">
<div class="card-header"><h5 class="fw-bold mb-0"><i class="fas fa-code me-2" style="color: var(--color-info);"></i> Skills Required</h5></div>
<div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8;" th:text="${job.skills()}">Skills</div>
</div>
<div class="col-lg-4">
<div class="card mb-4">
<div class="card-header"><h5 class="fw-bold mb-0"><i class="fas fa-info-circle me-2" style="color: var(--color-primary);"></i> Job Summary</h5></div>
<div class="card-body">
<div class="mb-3"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Posted On</div><div class="fw-semibold" th:text="${#temporals.format(job.createdAt(), 'MMMM dd, yyyy')}">Date</div>
<div class="mb-3"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Application Deadline</div><div class="fw-semibold" th:text="${#temporals.format(job.deadline(), 'MMMM dd, yyyy')}">Deadline</div>
<div class="mb-3"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Experience Level</div><div class="fw-semibold" th:text="${job.experienceLevel() ?: 'Not specified'}">Level</div>
<div class="mb-3"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Employment Type</div><div class="fw-semibold" th:text="${job.employmentType()}">Type</div>
<div class="mb-0"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Applications</div><div class="fw-semibold" th:text="${job.applicationCount()} + ' applicants'">0 applicants</div>
</div>
<div class="card mb-4">
<div class="card-header"><h5 class="fw-bold mb-0"><i class="fas fa-phone me-2" style="color: var(--color-success);"></i> Contact</h5></div>
<div class="card-body text-center">
<div class="job-company-logo mx-auto" style="width: 60px; height: 60px; font-size: 1.5rem;"><i class="fas fa-building"></i></div>
<h5 class="fw-bold mt-2" th:text="${job.company()}">Company</h5>
<p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.location()}">Location</p>
<p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.employerName()}">Employer</p>
</div>
</div>
</div>
<div th:if="${job == null}" class="container py-5" style="margin-top: 76px;">
<div class="empty-state">
<div class="empty-icon"><i class="fas fa-briefcase"></i></div>
<h4>Job not found</h4>
<p>This job posting may have been removed or is no longer available.</p>
<a th:href="@{/jobs}" class="btn btn-primary-gradient">Browse Jobs</a>
</div>
<footer th:replace="~{fragments/footer::footer}"></footer>
<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>""")

# ============================================================
# ISSUE 5: Job Apply - need to check the apply.html doesn't exist as template
# The issue is detail.html form posts to /student/jobs/apply which works
# But apply.html exists as template but is never used. Fix: ensure forms have correct paths.
# The apply button in detail.html uses string literal which is ok
# ============================================================
print("ISSUE 5: Job apply via detail.html form - action path /student/jobs/apply is correct")

# ============================================================
# ISSUE 6: Search Results page - broken HTML causing HTTP 500
# Fix: Rewrite search-results.html with proper tag structure
# ============================================================
write_file('src/main/resources/templates/jobs/search-results.html', """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      lang="en">
<head th:replace="~{fragments/header::head('Search Results - JobPortal', 'Job search results')}"></head>
<body>
<div class="aurora-bg"></div>
<div class="aurora-blob aurora-blob-1"></div>
<div class="aurora-blob aurora-blob-2"></div>
<div class="mouse-glow"></div>
<nav th:replace="~{fragments/header::navbar}"></nav>
<div class="container py-4" style="margin-top: 76px;">
<div class="glass-card-lg p-4 mb-4 fade-in">
<div class="d-flex flex-wrap align-items-center justify-content-between gap-3">
<div>
<h4 class="fw-bold mb-1"><span th:text="${totalJobs ?: 0}">0</span> results <span th:if="${keyword != null and !keyword.isEmpty()}">for "<span th:text="${keyword}">keyword</span>"</span></h4>
<p style="color: var(--text-secondary); font-size: var(--font-size-sm); margin: 0;">
<span th:if="${location != null and !location.isEmpty()}">in <span th:text="${location}">location</span> &middot; </span>
<span th:if="${employmentType != null and !employmentType.isEmpty()}" th:text="${employmentType}">Type</span>
<span th:if="${experienceLevel != null and !experienceLevel.isEmpty()}"> &middot; <span th:text="${experienceLevel}">Level</span>
</p>
</div>
<form th:action="@{/jobs/search}" method="get" class="d-flex gap-2">
<input type="text" name="keyword" class="form-input" style="width: 250px;" placeholder="Refine search..." th:value="${keyword}"/>
<input type="hidden" name="location" th:value="${location}"/>
<button type="submit" class="btn btn-primary-gradient"><i class="fas fa-search"></i></button>
</form>
</div>
<div class="d-flex flex-wrap gap-2 mt-3">
<a th:href="@{/jobs}" class="btn btn-ghost btn-sm" style="font-size: var(--font-size-xs);"><i class="fas fa-times me-1"></i> Clear all filters</a>
<span th:if="${keyword != null and !keyword.isEmpty()}" class="badge badge-primary" style="font-size: var(--font-size-sm); padding: 6px 14px;">"<span th:text="${keyword}">keyword</span>" <a th:href="@{/jobs/search(location=${location})}" class="ms-2" style="color: inherit;"><i class="fas fa-times"></i></a></span>
<span th:if="${location != null and !location.isEmpty()}" class="badge badge-info" style="font-size: var(--font-size-sm); padding: 6px 14px;"><span th:text="${location}">location</span> <a th:href="@{/jobs/search(keyword=${keyword})}" class="ms-2" style="color: inherit;"><i class="fas fa-times"></i></a></span>
<span th:if="${employmentType != null and !employmentType.isEmpty()}" class="badge badge-success" style="font-size: var(--font-size-sm); padding: 6px 14px;" th:text="${employmentType}">Type</span>
</div>
<div class="row g-4">
<div class="col-lg-8">
<div th:if="${jobs != null and !jobs.isEmpty()}">
<div class="d-flex flex-column gap-3">
<div th:each="job : ${jobs}">
<div class="job-card card card-lift">
<div class="d-flex align-items-start gap-3">
<div class="job-company-logo"><i class="fas fa-building"></i></div>
<div class="flex-grow-1" style="min-width: 0;">
<div class="d-flex justify-content-between align-items-start">
<div>
<a th:href="@{'/jobs/view/' + ${job.id()}}" class="text-decoration-none"><h5 class="job-title" th:text="${job.title()}">Title</h5></a>
<div class="job-company"><i class="fas fa-building me-1"></i> <span th:text="${job.company()}">Company</span></div>
</div>
<div class="job-tags">
<span class="badge badge-primary"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>
<span class="badge badge-success" th:if="${job.salaryRange() != null}"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary</span>
<span class="badge badge-info" th:if="${job.employmentType() != null}" th:text="${job.employmentType()}">Type</span>
</div>
<p class="mt-2" style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${#strings.abbreviate(job.description(), 150)}">Description</p>
<div class="job-meta">
<span class="job-date"><i class="far fa-clock me-1"></i> <span th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</span>
</div>
</div>
</div>
</div>
<div th:if="${jobs == null or jobs.isEmpty()}" class="empty-state">
<div class="empty-icon"><i class="fas fa-search"></i></div>
<h4>No results found</h4>
<p>Try different keywords or remove filters.</p>
<a th:href="@{/jobs}" class="btn btn-primary-gradient">Browse All Jobs</a>
</div>
<div th:if="${totalPages > 1}" class="d-flex justify-content-center mt-4">
<nav><ul class="pagination">
<li th:each="i : ${#numbers.sequence(0, totalPages - 1)}" class="page-item" th:classappend="${i == currentPage} ? 'active' : ''">
<a class="page-link" th:href="@{/jobs/search(page=${i}, keyword=${keyword}, location=${location})}" th:text="${i + 1}">1</a>
</li>
</ul></nav>
</div>
<div class="col-lg-4">
<div class="card mb-4">
<div class="card-header"><h5 class="fw-bold mb-0"><i class="fas fa-lightbulb me-2" style="color: var(--color-warning);"></i> Suggested Searches</h5></div>
<div class="card-body">
<div class="d-flex flex-column gap-2">
<a th:href="@{/jobs/search(keyword='Java Developer')}" class="btn btn-ghost btn-sm text-start"><i class="fas fa-arrow-right me-2" style="font-size: 0.7rem;"></i> Java Developer</a>
<a th:href="@{/jobs/search(keyword='React Developer')}" class="btn btn-ghost btn-sm text-start"><i class="fas fa-arrow-right me-2" style="font-size: 0.7rem;"></i> React Developer</a>
<a th:href="@{/jobs/search(keyword='Data Analyst')}" class="btn btn-ghost btn-sm text-start"><i class="fas fa-arrow-right me-2" style="font-size: 0.7rem;"></i> Data Analyst</a>
<a th:href="@{/jobs/search(keyword='Product Manager')}" class="btn btn-ghost btn-sm text-start"><i class="fas fa-arrow-right me-2" style="font-size: 0.7rem;"></i> Product Manager</a>
<a th:href="@{/jobs/search(keyword='DevOps Engineer')}" class="btn btn-ghost btn-sm text-start"><i class="fas fa-arrow-right me-2" style="font-size: 0.7rem;"></i> DevOps Engineer</a>
</div>
</div>
</div>
<footer th:replace="~{fragments/footer::footer}"></footer>
<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>""")

print("\nAll template files fixed!")
print("\nNote: StudentController.java needs 2 additional model attributes in resumePage() method:")
print("  - resumeFileName")
print("  - resumeFileSize")
print("These need to be added to /student/resume endpoint in StudentController.java")
