import re

# Write the corrected dashboard HTML
html = '<!DOCTYPE html>\n'
html += '<html xmlns:th="http://www.thymeleaf.org"\n'
html += '      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"\n'
html += '      lang="en">\n'
html += '<head th:replace="~{fragments/header::head(\'Employer Dashboard - JobPortal\', \'Your recruitment dashboard\')}"></head>\n'
html += '<body>\n'
html += '<div th:replace="~{fragments/employer-sidebar::sidebar(\'Dashboard\', \'dashboard\')}"></div>\n'
html += '<div class="dashboard-main">\n'
html += '    <div class="container-fluid">\n'
html += '        <div class="glass-card-lg p-5 mb-5 fade-in">\n'
html += '            <div class="row align-items-center">\n'
html += '                <div class="col-lg-8">\n'
html += '                    <div class="d-flex align-items-center gap-4 mb-3">\n'
html += '                        <div class="profile-avatar" style="width: 72px; height: 72px; font-size: 2rem;">\n'
html += '                            <span th:text="${currentUser != null ? #strings.substring(currentUser.fullName, 0, 1) : \'E\'}">E</span>\n'
html += '                        </div>\n'
html += '                        <div>\n'
html += '                            <h2 class="fw-bold mb-1" style="font-size: 1.8rem;">Welcome back, <span th:text="${currentUser?.fullName ?: \'Employer\'}">Employer</span></h2>\n'
html += '                            <p style="color: var(--text-secondary);">Your recruitment hub. Manage jobs, review applicants, and find top talent.</p>\n'
html += '                        </div>\n'
html += '                    </div>\n'
html += '                    <div class="d-flex flex-wrap gap-3 mt-4">\n'
html += '                        <a th:href="@{/employer/jobs/post}" class="btn btn-primary-gradient"><i class="fas fa-plus"></i> Post New Job</a>\n'
html += '                        <a th:href="@{/employer/applicants}" class="btn btn-secondary-glass"><i class="fas fa-users"></i> View Applicants</a>\n'
html += '                    </div>\n'
html += '                </div>\n'
html += '                <div class="col-lg-4 d-none d-lg-block">\n'
html += '                    <div class="glass p-4 text-center">\n'
html += '                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">Jobs Posted</div>\n'
html += '                        <div style="font-size: 2.5rem; font-weight: 800; color: var(--color-primary);" th:text="${stats?.employerJobCount ?: 0}">0</div>\n'
html += '                    </div>\n'
html += '                </div>\n'
html += '            </div>\n'
html += '        </div>\n'
html += '    </div>\n'
html += '</div>\n'
html += '<div th:replace="~{fragments/header::scripts}"></div>\n'
html += '</body>\n'
html += '</html>\n'

with open('src/main/resources/templates/employer/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(html)

# Verify
with open('src/main/resources/templates/employer/dashboard.html', encoding='utf-8') as f:
    c = f.read()
    
print(f'File size: {len(c)} bytes')
opens = len(re.findall(r'<div(?:\s|>)', c))
closes = c.count('</div>')
print(f'divs: {opens} open, {closes} close (diff={opens-closes})')

# Check Java controller
with open('src/main/java/com/onlinejobportal/controller/EmployerController.java', encoding='utf-8') as f:
    java = f.read()
print(f'\nController has try-catch: {"try {" in java and "catch (Exception e)" in java}')
print(f'Controller has DashboardStatsResponse import: {"import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;" in java}')
print(f'Controller has empty(): {"DashboardStatsResponse.empty()" in java}')
