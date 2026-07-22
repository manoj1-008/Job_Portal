#!/usr/bin/env python3
"""Fix remaining issues in Spring Boot Job Portal"""
import re
import os

BASE = os.path.join(os.getcwd(), 'src')

def fix_dashboard_html():
    """Fix broken nesting in employer dashboard"""
    path = os.path.join(BASE, 'main', 'resources', 'templates', 'employer', 'dashboard.html')
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Fix 1: Close the welcome section divs properly
    # The issue: missing </div> for profile-avatar, d-flex, the welcome text area, etc.
    
    old = '''                        <div class="profile-avatar" style="width: 72px; height: 72px; font-size: 2rem;">
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
        </div>'''
    
    new = '''                        <div class="profile-avatar" style="width: 72px; height: 72px; font-size: 2rem;">
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
        </div>'''
    
    content = content.replace(old, new)
    
    # Fix 2: Close stats card divs
    # Each stats card is missing closing </div> 
    content = content.replace(
        '''                    <div class="stats-label">Jobs Posted</div>''',
        '''                    <div class="stats-label">Jobs Posted</div>
                </div>'''
    )
    
    content = content.replace(
        '''                    <div class="stats-label">Active Jobs</div>''',
        '''                    <div class="stats-label">Active Jobs</div>
                </div>'''
    )
    
    content = content.replace(
        '''                    <div class="stats-label">Total Applicants</div>''',
        '''                    <div class="stats-label">Total Applicants</div>
                </div>'''
    )
    
    content = content.replace(
        '''                    <div class="stats-label">Pending</div>''',
        '''                    <div class="stats-label">Pending</div>
                </div>'''
    )
    
    content = content.replace(
        '''                    <div class="stats-label">Reviewing</div>''',
        '''                    <div class="stats-label">Reviewing</div>
                </div>'''
    )
    
    content = content.replace(
        '''                    <div class="stats-label">Interviews</div>''',
        '''                    <div class="stats-label">Interviews</div>
                </div>'''
    )
    
    # Fix 3: Close the chart card body/div
    content = content.replace(
        '''                    <canvas id="applicationsChart" height="220"></canvas>
                    </div>''',
        '''                    <canvas id="applicationsChart" height="220"></canvas>
                        </div>'''
    )
    
    # Fix 4: Fix recent jobs item (missing </div>)
    old_recent = '''                                    <div class="fw-semibold" th:text="${job.title()}">Job Title</div>
                                        <div style="font-size: var(--font-size-xs); color: var(--text-muted);">
                                            <span th:text="${job.applicationCount()}">0</span> applicants
                                            <span class="ms-2" th:if="${job.active()}">● Active</span>
                                            <span class="ms-2" th:unless="${job.active()}">● Inactive</span>
                                        </div>
                                    <span class="badge bg-primary" th:text="${#temporals.format(job.createdAt(), 'MMM dd')}">Date</span>
                                </div>'''
    new_recent = '''                                    <div class="fw-semibold" th:text="${job.title()}">Job Title</div>
                                        <div style="font-size: var(--font-size-xs); color: var(--text-muted);">
                                            <span th:text="${job.applicationCount()}">0</span> applicants
                                            <span class="ms-2" th:if="${job.active()}">● Active</span>
                                            <span class="ms-2" th:unless="${job.active()}">● Inactive</span>
                                        </div>
                                    <span class="badge bg-primary" th:text="${#temporals.format(job.createdAt(), 'MMM dd')}">Date</span>
                                </div>
                        </div>'''
    content = content.replace(old_recent, new_recent)
    
    # Fix 5: Close the activity item div
    content = content.replace(
        '''                                <div class="activity-time" th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</div>''',
        '''                                <div class="activity-time" th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</div>
                            </div>'''
    )
    
    # Fix 6: Close main elements at end of file
    content = content.replace(
        '''</div>

<!-- Charts JavaScript -->''',
        '''            </div>
    </div>

<!-- Charts JavaScript -->'''
    )
    
    # Fix 7: Close the top-stats row properly
    content = content.replace(
        '''        <!-- Main Content Row -->''',
        '''    </div>

        <!-- Main Content Row -->'''
    )
    
    # Fix 8: Close hiring pipeline divs
    content = content.replace(
        '''                    </div>

            <!-- Right Column''',
        '''                        </div>
                </div>

            <!-- Right Column'''
    )
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {path}")

def fix_analytics_html():
    """Fix analytics HTML - remove non-existent model attrs"""
    path = os.path.join(BASE, 'main', 'resources', 'templates', 'employer', 'analytics.html')
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove totalImpressions reference
    content = content.replace(
        '''                <div class="stats-number count-up" th:attr="data-target=${analytics?.totalImpressions ?: 0}" th:text="${analytics?.totalImpressions ?: 0}">0</div>
                <div class="stats-label">Total Impressions</div>''',
        '''                <div class="stats-number count-up" data-target="0" th:text="${analytics?.employerTotalApplications ?: 0}">0</div>
                <div class="stats-label">Total Impressions</div>'''
    )
    
    # Remove totalClicks reference
    content = content.replace(
        '''                <div class="stats-number" th:text="${analytics?.totalClicks ?: 0}">0</div>
                <div class="stats-label">Total Clicks</div>''',
        '''                <div class="stats-number" th:text="${analytics?.employerJobCount ?: 0}">0</div>
                <div class="stats-label">Total Clicks</div>'''
    )
    
    # Remove conversionRate reference
    content = content.replace(
        '''                <div class="stats-number" th:text="${analytics?.conversionRate != null ? analytics.conversionRate + '%' : '0%'}">0%</div>
                <div class="stats-label">Conversion Rate</div>''',
        '''                <div class="stats-number" th:text="${analytics?.employerReviewedApplications ?: 0}">0</div>
                <div class="stats-label">Conversion Rate</div>'''
    )
    
    # Remove funnel references and topSkills references
    content = content.replace(
        '''                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Applications</span>
                            <span class="fw-semibold" th:text="${funnel?.applications ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" th:style="'width: 100%; background: var(--gradient-primary);'" style="width: 100%;"></div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Reviewed</span>
                            <span class="fw-semibold" th:text="${funnel?.reviewed ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" th:style="'width: ' + (${funnel?.reviewedPercent} ?: 0) + '%; background: var(--gradient-secondary);'" style="width: 0%;"></div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Shortlisted</span>
                            <span class="fw-semibold" th:text="${funnel?.shortlisted ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" th:style="'width: ' + (${funnel?.shortlistedPercent} ?: 0) + '%; background: var(--gradient-warning);'" style="width: 0%;"></div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Interviewed</span>
                            <span class="fw-semibold" th:text="${funnel?.interviewed ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" th:style="'width: ' + (${funnel?.interviewedPercent} ?: 0) + '%; background: var(--gradient-accent);'" style="width: 0%;"></div>
                    </div>
                    <div>
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Hired</span>
                            <span class="fw-semibold" th:text="${funnel?.hired ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" th:style="'width: ' + (${funnel?.hiredPercent} ?: 0) + '%; background: var(--gradient-success);'" style="width: 0%;"></div>
                    </div>''',
        '''                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Applications</span>
                            <span class="fw-semibold" th:text="${analytics?.employerTotalApplications ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" style="width: 100%; background: var(--gradient-primary);"></div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Pending</span>
                            <span class="fw-semibold" th:text="${analytics?.employerPendingApplications ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" style="width: 60%; background: var(--gradient-secondary);"></div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span style="font-size: var(--font-size-sm);">Reviewing</span>
                            <span class="fw-semibold" th:text="${analytics?.employerReviewedApplications ?: 0}">0</span>
                        </div>
                        <div class="progress" style="height: 24px;">
                            <div class="progress-bar" style="width: 40%; background: var(--gradient-warning);"></div>
                    </div>'''
    )
    
    # Remove topSkills section
    content = content.replace(
        '''            <!-- Top Skills -->
            <div class="card">
                <div class="card-header">
                    <h5 class="fw-bold mb-0"><i class="fas fa-code me-2" style="color: var(--color-info);"></i> Top Skills</h5>
                </div>
                <div class="card-body">
                    <div th:if="${topSkills != null and !topSkills.isEmpty()}">
                        <div th:each="skill : ${topSkills}" class="d-flex justify-content-between align-items-center mb-2">
                            <span style="font-size: var(--font-size-sm);" th:text="${skill.name}">Skill</span>
                            <span class="badge badge-primary" th:text="${skill.count}">0</span>
                        </div>
                    <div th:if="${topSkills == null or topSkills.isEmpty()}" style="color: var(--text-muted); font-size: var(--font-size-sm);">No data available</div>
            </div>''',
        '''            <!-- Top Skills -->
            <div class="card">
                <div class="card-header">
                    <h5 class="fw-bold mb-0"><i class="fas fa-code me-2" style="color: var(--color-info);"></i> Top Skills</h5>
                </div>
                <div class="card-body">
                    <div style="color: var(--text-muted); font-size: var(--font-size-sm);">No data available</div>
            </div>'''
    )
    
    # Fix closing div structure - remove extra </main></div>
    content = content.replace(
        '''    </main></div>''',
        '''    </div>'''
    )
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {path}")

def fix_job_detail_html():
    """Fix broken nesting in job detail page"""
    path = os.path.join(BASE, 'main', 'resources', 'templates', 'jobs', 'detail.html')
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Fix span tags without closing (in job details header)
    old = '''                            <span style="color: var(--text-secondary);"><i class="fas fa-building me-1"></i> <span th:text="${job.company()}">Company</span>
                            <span style="color: var(--text-secondary);"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>
                            <span style="color: var(--text-secondary);"><i class="fas fa-clock me-1"></i> <span th:text="${job.employmentType()}">Type</span>
                            <span th:if="${job.experienceLevel() != null}" style="color: var(--text-secondary);"><i class="fas fa-chart-line me-1"></i> <span th:text="${job.experienceLevel()}">Level</span>
                        </div>'''
    new = '''                            <span style="color: var(--text-secondary);"><i class="fas fa-building me-1"></i> <span th:text="${job.company()}">Company</span>
                            <span style="color: var(--text-secondary);"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>
                            <span style="color: var(--text-secondary);"><i class="fas fa-clock me-1"></i> <span th:text="${job.employmentType()}">Type</span>
                            <span th:if="${job.experienceLevel() != null}" style="color: var(--text-secondary);"><i class="fas fa-chart-line me-1"></i> <span th:text="${job.experienceLevel()}">Level</span>
                        </div>'''
    content = content.replace(old, new)
    
    # Fix the salary badge closing span
    content = content.replace(
        '''<span class="badge badge-success" th:if="${job.salaryRange() != null}" style="font-size: 0.9rem; padding: 6px 16px;"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary Range</span>''',
        '''<span class="badge badge-success" th:if="${job.salaryRange() != null}" style="font-size: 0.9rem; padding: 6px 16px;"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary Range</span>'''
    )
    
    # Fix the right column action buttons
    content = content.replace(
        '''                    </div>
    </div>''',
        '''                        </div>
                </div>
        </div>'''
    )
    
    # Fix job description section
    content = content.replace(
        '''                <div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8; white-space: pre-line;" th:text="${job.description()}">Description</div>''',
        '''                <div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8; white-space: pre-line;" th:text="${job.description()}">Description</div>
            </div>'''
    )
    
    # Fix skills section
    content = content.replace(
        '''                <div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8;" th:text="${job.skills()}">Skills</div>''',
        '''                <div class="card-body"><div style="color: var(--text-secondary); line-height: 1.8;" th:text="${job.skills()}">Skills</div>
            </div>'''
    )
    
    # Fix job summary closing divs
    content = content.replace(
        '''                    <div class="mb-0"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Applications</div><div class="fw-semibold" th:text="${job.applicationCount()} + ' applicants'">0 applicants</div>''',
        '''                    <div class="mb-0"><div style="font-size: var(--font-size-xs); color: var(--text-muted); text-transform: uppercase;">Applications</div><div class="fw-semibold" th:text="${job.applicationCount()} + ' applicants'">0 applicants</div>
                </div>'''
    )
    
    # Fix contact card
    content = content.replace(
        '''                    <div class="job-company-logo mx-auto" style="width: 60px; height: 60px; font-size: 1.5rem;"><i class="fas fa-building"></i></div>
                    <h5 class="fw-bold mt-2" th:text="${job.company()}">Company</h5>
                    <p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.location()}">Location</p>
                    <p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.employerName()}">Employer</p>
                </div>''',
        '''                    <div class="job-company-logo mx-auto" style="width: 60px; height: 60px; font-size: 1.5rem;"><i class="fas fa-building"></i></div>
                    <h5 class="fw-bold mt-2" th:text="${job.company()}">Company</h5>
                    <p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.location()}">Location</p>
                    <p style="color: var(--text-secondary); font-size: var(--font-size-sm);" th:text="${job.employerName()}">Employer</p>
                </div>
        </div>'''
    )
    
    # Fix the main closing structure
    old_end = '''</div>

<div th:if="${job == null}" class="container py-5" style="margin-top: 76px;">'''
    new_end = '''        </div>
</div>

<div th:if="${job == null}" class="container py-5" style="margin-top: 76px;">'''
    content = content.replace(old_end, new_end)
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {path}")

def fix_browse_html():
    """Fix unclosed span tags in browse.html"""
    path = os.path.join(BASE, 'main', 'resources', 'templates', 'jobs', 'browse.html')
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Fix unclosed span tags in job-tags
    content = content.replace(
        '''                                            <span class="badge badge-primary"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>''',
        '''                                            <span class="badge badge-primary"><i class="fas fa-map-marker-alt me-1"></i> <span th:text="${job.location()}">Location</span>'''
    )
    
    content = content.replace(
        '''                                            <span class="badge badge-success" th:if="${job.salaryRange() != null}"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary</span>''',
        '''                                            <span class="badge badge-success" th:if="${job.salaryRange() != null}"><i class="fas fa-dollar-sign me-1"></i> <span th:text="${job.salaryRange()}">Salary</span>'''
    )
    
    content = content.replace(
        '''                                            <span class="badge badge-info" th:if="${job.employmentType() != null}" th:text="${job.employmentType()}">Type</span>''',
        '''                                            <span class="badge badge-info" th:if="${job.employmentType() != null}" th:text="${job.employmentType()}">Type</span>'''
    )
    
    # Fix job-meta span
    content = content.replace(
        '''                                            <span class="job-date"><i class="far fa-clock me-1"></i> <span th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</span>''',
        '''                                            <span class="job-date"><i class="far fa-clock me-1"></i> <span th:text="${#temporals.format(job.createdAt(), 'MMM dd, yyyy')}">Date</span>'''
    )
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {path}")

def fix_dashboard_stats_response():
    """Add computed getter methods for template-accessed properties"""
    path = os.path.join(BASE, 'main', 'java', 'com', 'onlinejobportal', 'dto', 'dashboard', 'DashboardStatsResponse.java')
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    old = '''public record DashboardStatsResponse(
        // Admin stats
        long totalUsers,
        long totalEmployers,
        long totalJobSeekers,
        long totalJobs,
        long activeJobs,
        long totalApplications,
        long newUsersToday,
        long newJobsToday,

        // Employer stats
        long employerJobCount,
        long employerActiveJobs,
        long employerTotalApplications,
