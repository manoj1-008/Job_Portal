#!/usr/bin/env python3
# Fix 3 issues: profile.html error, stats card visibility, resume page duplicate header

import os

def write_file(path, content):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Written {len(content)} bytes to {path}')

# ===== ISSUE 1: student/profile.html - Fix Thymeleaf structure =====
# Convert to use dashboard-main pattern (consistent with other dashboards)
content = '<!DOCTYPE html>\n'
content += '<html xmlns:th="http://www.thymeleaf.org"\n      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"\n      lang="en">\n'
content += '<head th:replace="~{fragments/header::head(\'My Profile - JobPortal\', \'Manage your profile\')}"></head>\n'
content += '<body>\n'
content += '    <div th:replace="~{fragments/student-sidebar::sidebar(\'Profile\', \'profile\')}"></div>\n'
content += '    <div class="dashboard-main">\n'
content += '        <div class="container-fluid">\n'
content += '            <div class="glass-card-lg p-5 mb-5 fade-in">\n'
content += '                <div class="d-flex justify-content-between align-items-center mb-5">\n'
content += '                    <h4 class="fw-bold mb-0"><i class="fas fa-user-circle me-2" style="color: var(--color-primary);"></i> My Profile</h4>\n'
content += '                    <button class="btn btn-primary-gradient" data-bs-toggle="modal" data-bs-target="#editProfileModal"><i class="fas fa-edit"></i> Edit Profile</button>\n'
content += '                </div>\n'
content += '                <div class="row g-4">\n'
content += '                    <div class="col-md-4 text-center">\n'
content += '                        <div class="profile-avatar mx-auto mb-3" style="width: 100px; height: 100px;"><span th:text="${studentProfile != null ? #strings.substring(studentProfile.fullName(), 0, 1) : \'U\'}">U</span></div>\n'
content += '                        <h5 class="fw-bold mb-1" th:text="${studentProfile.fullName()}">Full Name</h5>\n'
content += '                        <span class="badge badge-primary">Student</span>\n'
content += '                    </div>\n'
content += '                    <div class="col-md-8">\n'
content += '                        <div class="row g-3">\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Email</label>\n'
content += '                                <p class="fw-semibold mb-0" style="word-break: break-all; overflow-wrap: break-word;" th:text="${studentProfile.email()}">email@example.com</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Phone</label>\n'
content += '                                <p class="fw-semibold mb-0" style="word-break: break-all; overflow-wrap: break-word;" th:text="${studentProfile.phone()}">+1 234 567 890</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-12">\n'
content += '                                <label class="form-label text-muted small mb-1">Bio</label>\n'
content += '                                <p class="mb-0" th:text="${studentProfile.bio()}">Bio description goes here...</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Skills</label>\n'
content += '                                <p class="mb-0" th:text="${studentProfile.skills()}">Java, Spring Boot, SQL</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Experience Level</label>\n'
content += '                                <p class="mb-0" th:text="${studentProfile.experienceLevel()}">Entry Level</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Education</label>\n'
content += '                                <p class="mb-0" th:text="${studentProfile.education()}">Bachelor&#39;s Degree</p>\n'
content += '                            </div>\n'
content += '                            <div class="col-sm-6">\n'
content += '                                <label class="form-label text-muted small mb-1">Location</label>\n'
content += '                                <p class="mb-0" th:text="${studentProfile.location()}">New York, USA</p>\n'
content += '                            </div>\n'
content += '                        </div>\n'
content += '                    </div>\n'
content += '                </div>\n'
content += '            </div>\n'
content += '        </div>\n'
content += '    </div>\n'
content += '    <div th:replace="~{fragments/header::scripts}"></div>\n'
content += '</body>\n'
content += '</html>\n'
write_file('src/main/resources/templates/student/profile.html', content)

# ===== ISSUE 2: Dashboard stats cards invisible - Fix CSS =====
css_path = 'src/main/resources/static/css/style.css'
with open(css_path, 'r') as f:
    css = f.read()

# Add explicit color to .stats-card .stats-number
old = """    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: var(--radius-2xl);
    padding: var(--space-6);
    transition: all var(--transition-normal);
}

.stats-card:hover {
    background: var(--bg-card-hover);
    box-shadow: var(--shadow-md);
}

.stats-card .stats-icon {
    width: 48px;
    height: 48px;
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.3rem;
    margin-bottom: var(--space-4);
}

.stats-card .stats-icon.primary { background: rgba(139, 92, 246, 0.15); color: var(--color-primary); }
.stats-card .stats-icon.secondary { background: rgba(59, 130, 246, 0.15); color: var(--color-secondary); }
.stats-card .stats-icon.success { background: rgba(16, 185, 129, 0.15); color: var(--color-success); }
.stats-card .stats-icon.danger { background: rgba(239, 68, 68, 0.15); color: var(--color-danger); }
.stats-card .stats-icon.warning { background: rgba(245, 158, 11, 0.15); color: var(--color-warning); }
.stats-card .stats-icon.info { background: rgba(6, 182, 212, 0.15); color: var(--color-info); }
.stats-card .stats-icon.pink { background: rgba(236, 72, 153, 0.15); color: var(--color-pink); }

.stats-card .stats-number {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-extrabold);
    line-height: 1;
    margin-bottom: var(--space-1);
}

.stats-card .stats-label {
    color: var(--text-secondary);
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-medium);
}"""

new = """    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: var(--radius-2xl);
    padding: var(--space-6);
    transition: all var(--transition-normal);
}

.stats-card:hover {
    background: var(--bg-card-hover);
    box-shadow: var(--shadow-md);
}

.stats-card .stats-icon {
    width: 48px;
    height: 48px;
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.3rem;
    margin-bottom: var(--space-4);
}

.stats-card .stats-icon.primary { background: rgba(139, 92, 246, 0.15); color: var(--color-primary); }
.stats-card .stats-icon.secondary { background: rgba(59, 130, 246, 0.15); color: var(--color-secondary); }
.stats-card .stats-icon.success { background: rgba(16, 185, 129, 0.15); color: var(--color-success); }
.stats-card .stats-icon.danger { background: rgba(239, 68, 68, 0.15); color: var(--color-danger); }
.stats-card .stats-icon.warning { background: rgba(245, 158, 11, 0.15); color: var(--color-warning); }
.stats-card .stats-icon.info { background: rgba(6, 182, 212, 0.15); color: var(--color-info); }
.stats-card .stats-icon.pink { background: rgba(236, 72, 153, 0.15); color: var(--color-pink); }

.stats-card .stats-number {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-extrabold);
    line-height: 1;
    margin-bottom: var(--space-1);
    color: var(--text-primary) !important;
}

.stats-card .stats-label {
    color: var(--text-secondary);
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-medium);
}"""

if old in css:
    css = css.replace(old, new)
    with open(css_path, 'w', encoding='utf-8') as f:
        f.write(css)
    print(f'Fixed .stats-number color in {css_path}')
else:
    print(f'WARNING: Could not find .stats-number section in CSS - checking alternative patterns')
    # Check if color already exists
    if 'stats-card .stats-number' in css and 'color:' in css[css.find('stats-card .stats-number'):css.find('stats-card .stats-number')+200]:
        print('  color property already exists in .stats-number')
    else:
        print('  Could not locate the section to update')

# ===== ISSUE 3: Resume page duplicate header - Fix structure =====
content = '<!DOCTYPE html>\n'
content += '<html xmlns:th="http://www.thymeleaf.org"\n'
content += '      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"\n'
content += '      lang="en">\n'
content += '<head th:replace="~{fragments/header::head(\'Resume - JobPortal\', \'Upload your resume\')}"></head>\n'
content += '<body>\n'
content += '    <div th:replace="~{fragments/student-sidebar::sidebar(\'Resume\', \'resume\')}"></div>\n'
content += '    <div class="dashboard-main">\n'
content += '        <div class="container-fluid">\n'
content += '            <div class="row g-4">\n'
content += '                <div class="col-lg-8">\n'
content += '                    <div class="card mb-4">\n'
content += '                        <div class="card-header">\n'
content += '                            <h5 class="fw-bold mb-0"><i class="fas fa-upload me-2" style="color: var(--color-primary);"></i> Upload Resume</h5>\n'
content += '                        </div>\n'
content += '                        <div class="card-body">\n'
content += '                            <form th:action="@{/student/resume/upload}" method="post" enctype="multipart/form-data">\n'
content += '                                <div class="file-upload mb-4" id="fileDropZone">\n'
content += '                                    <i class="fas fa-cloud-upload-alt"></i>\n'
content += '                                    <h5 class="fw-semibold mb-2">Drag &amp; drop your resume here</h5>\n'
content += '                                    <p>or click to browse files</p>\n'
content += '                                    <p style="font-size: var(--font-size-xs); color: var(--text-muted);">\n'
content += '                                        Supported formats: PDF, DOC, DOCX (Max: 10MB)\n'
content += '                                    </p>\n'
content += '                                    <input type="file" name="file" id="resumeFile" class="d-none" accept=".pdf,.doc,.docx" required>\n'
content += '                                    <button type="button" class="btn btn-primary-gradient" id="browseBtn">\n'
content += '                                        <i class="fas fa-folder-open"></i> Browse Files\n'
content += '                                    </button>\n'
content += '                                </div>\n'
content += '                                <div id="filePreview" class="glass p-4 mb-4" style="border-radius: var(--radius-lg); display: none;">\n'
content += '                                    <div class="d-flex align-items-center gap-3">\n'
content += '                                        <div style="font-size: 2rem; color: var(--color-danger);"><i class="fas fa-file-pdf"></i></div>\n'
content += '                                        <div class="flex-grow-1">\n'
content += '                                            <div class="fw-semibold" id="fileName">filename.pdf</div>\n'
content += '                                            <div style="font-size: var(--font-size-xs); color: var(--text-muted);" id="fileSize">0 KB</div>\n'
content += '                                        </div>\n'
content += '                                        <button type="button" class="btn btn-danger btn-sm" id="removeFile">\n'
content += '                                            <i class="fas fa-times"></i> Remove\n'
content += '                                        </button>\n'
content += '                                    </div>\n'
content += '                                </div>\n'
content += '                                <button type="submit" class="btn btn-primary-gradient btn-lg w-100" id="uploadBtn" disabled>\n'
content += '                                    <i class="fas fa-cloud-upload-alt"></i>\n'
content += '                                    <span id="uploadBtnText">Upload Resume</span>\n'
content += '                                </button>\n'
content += '                            </form>\n'
content += '                        </div>\n'
content += '                    </div>\n'
content += '                </div>\n'
content += '                <div class="col-lg-4">\n'
content += '                    <div class="card mb-4">\n'
content += '                        <div class="card-header">\n'
content += '                            <h5 class="fw-bold mb-0"><i class="fas fa-file-pdf me-2" style="color: var(--color-danger);"></i> Current Resume</h5>\n'
content += '                        </div>\n'
content += '                        <div class="card-body text-center">\n'
content += '                            <div th:if="${hasResume != null and hasResume}">\n'
content += '                                <div style="font-size: 3rem; color: var(--color-danger); opacity: 0.6;"><i class="fas fa-file-pdf"></i></div>\n'
content += '                                <div class="fw-semibold mt-2" th:text="${resumeFileName ?: \'resume.pdf\'}">resume.pdf</div>\n'
content += '                                <div style="font-size: var(--font-size-xs); color: var(--text-muted);" th:text="${resumeFileSize ?: \'2.4 MB\'}">size</div>\n'
content += '                                <div class="d-flex gap-2 mt-3">\n'
content += '                                    <a th:href="@{/student/resume/download}" class="btn btn-primary-gradient btn-sm flex-grow-1"><i class="fas fa-download"></i> Download</a>\n'
content += '                                    <a th:href="@{/student/resume/delete}" class="btn btn-danger btn-sm"><i class="fas fa-trash"></i></a>\n'
content += '                                </div>\n'
content += '                            </div>\n'
content += '                            <div th:if="${hasResume == null or !hasResume}">\n'
content += '                                <div style="font-size: 3rem; color: var(--text-muted); opacity: 0.4;"><i class="fas fa-file-pdf"></i></div>\n'
content += '                                <p style="color: var(--text-secondary); font-size: var(--font-size-sm); margin-top: 0.5rem;">No resume uploaded</p>\n'
content += '                            </div>\n'
content += '                        </div>\n'
content += '                    </div>\n'
content += '                    <div class="card">\n'
content += '                        <div class="card-header">\n'
content += '                            <h5 class="fw-bold mb-0"><i class="fas fa-lightbulb me-2" style="color: var(--color-warning);"></i> Resume Tips</h5>\n'
content += '                        </div>\n'
content += '                        <div class="card-body">\n'
content += '                            <ul style="font-size: var(--font-size-sm); color: var(--text-secondary); line-height: 1.8; padding-left: 1.2rem;">\n'
content += '                                <li>Use a clear, professional file name</li>\n'
content += '                                <li>Keep your resume to 1-2 pages</li>\n'
content += '                                <li>Highlight relevant skills and experience</li>\n'
content += '                                <li>Use bullet points for readability</li>\n'
content += '                                <li>Include quantifiable achievements</li>\n'
content += '                                <li>Proofread for errors</li>\n'
content += '                            </ul>\n'
content += '                        </div>\n'
content += '                    </div>\n'
content += '                </div>\n'
content += '            </div>\n'
content += '        </div>\n'
content += '    </div>\n'
content += '    <div th:replace="~{fragments/header::scripts}"></div>\n'
content += '    <script>\n'
content += '    document.addEventListener(\'DOMContentLoaded\', function() {\n'
content += '        const dropZone = document.getElementById(\'fileDropZone\');\n'
content += '        const fileInput = document.getElementById(\'resumeFile\');\n'
content += '        const browseBtn = document.getElementById(\'browseBtn\');\n'
content += '        const filePreview = document.getElementById(\'filePreview\');\n'
content += '        const fileName = document.getElementById(\'fileName\');\n'
content += '        const fileSize = document.getElementById(\'fileSize\');\n'
content += '        const removeBtn = document.getElementById(\'removeFile\');\n'
content += '        const uploadBtn = document.getElementById(\'uploadBtn\');\n'
content += '        const uploadBtnText = document.getElementById(\'uploadBtnText\');\n'
content += '        if (browseBtn && fileInput) browseBtn.addEventListener(\'click\', () => fileInput.click());\n'
content += '        if (dropZone && fileInput) {\n'
content += '            dropZone.addEventListener(\'click\', (e) => { if (e.target === dropZone) fileInput.click(); });\n'
content += '            dropZone.addEventListener(\'dragover\', (e) => { e.preventDefault(); });\n'
content += '            dropZone.addEventListener(\'drop\', (e) => { e.preventDefault(); if (e.dataTransfer.files.length) { fileInput.files = e.dataTransfer.files; handleFile(e.dataTransfer.files[0]); } });\n'
content += '            fileInput.addEventListener(\'change\', () => { if (fileInput.files.length) handleFile(fileInput.files[0]); });\n'
content += '        }\n'
content += '        function handleFile(file) {\n'
content += '            if (filePreview) filePreview.style.display = \'\';\n'
content += '            if (fileName) fileName.textContent = file.name;\n'
content += '            if (fileSize) fileSize.textContent = (file.size / 1024).toFixed(1) + \' KB\';\n'
content += '            if (uploadBtn) uploadBtn.disabled = false;\n'
content += '            if (uploadBtnText) uploadBtnText.textContent = \'Upload \' + file.name;\n'
content += '        }\n'
content += '        if (removeBtn) removeBtn.addEventListener(\'click\', () => {\n'
content += '            if (fileInput) fileInput.value = \'\';\n'
content += '            if (filePreview) filePreview.style.display = \'none\';\n'
content += '            if (uploadBtn) uploadBtn.disabled = true;\n'
content += '            if (uploadBtnText) uploadBtnText.textContent = \'Upload Resume\';\n'
content += '        });\n'
content += '    });\n'
content += '    </script>\n'
content += '</body>\n'
content += '</html>\n'
write_file('src/main/resources/templates/student/resume-upload.html', content)

print('\nAll 3 issues fixed!')
