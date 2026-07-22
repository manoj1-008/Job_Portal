import sys
import os

student_html = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security" lang="en">
<head th:replace="~{fragments/header::head('Student Registration - JobPortal', 'Create your job seeker account')}"></head>
<body>
<div class="aurora-bg"></div>
<div class="aurora-blob aurora-blob-1"></div>
<div class="aurora-blob aurora-blob-2"></div>
<div class="mouse-glow"></div>
<div class="auth-container">
<div class="container">
<div class="row justify-content-center">
<div class="col-lg-6">
<div class="glass-card-lg auth-card">
<div class="text-center mb-4">
<a th:href="@{/}" class="d-inline-block mb-3" style="font-size: 2rem;"><i class="fas fa-user-graduate" style="color: var(--color-primary);"></i></a>
<h2 class="auth-title">Create Student Account</h2>
<p class="auth-subtitle">Start your journey to finding the perfect career</p>
</div>

<div th:if="${errorMessage}" class="alert alert-danger mb-4">
<i class="fas fa-exclamation-circle alert-icon"></i>
<div class="alert-content" th:text="${errorMessage}">Error</div>

<div th:if="${emailError}" class="alert alert-danger mb-4">
<i class="fas fa-exclamation-circle alert-icon"></i>
<div class="alert-content" th:text="${emailError}">Email already registered</div>

<form th:action="@{/student/register}" th:object="${registerRequest}" method="post" novalidate>
<input type="hidden" name="role" value="ROLE_JOBSEEKER" />

<div class="form-group">
<label class="form-label" for="fullName">Full Name</label>
<input type="text" id="fullName" th:field="*{fullName}" class="form-input" placeholder="John Doe" th:classappend="${#fields.hasErrors('fullName')} ? 'is-invalid' : ''" required />
<div th:if="${#fields.hasErrors('fullName')}" class="form-error" th:errors="*{fullName}">Error</div>

<div class="form-group">
<label class="form-label" for="email">Email Address</label>
<input type="email" id="email" th:field="*{email}" class="form-input" placeholder="you@example.com" th:classappend="${#fields.hasErrors('email')} ? 'is-invalid' : ''" required />
<div th:if="${#fields.hasErrors('email')}" class="form-error" th:errors="*{email}">Error</div>

<div class="row g-3">
<div class="col-md-6">
<div class="form-group">
<label class="form-label" for="password">Password</label>
<input type="password" id="passwordInput" th:field="*{password}" class="form-input" placeholder="Create a strong password" th:classappend="${#fields.hasErrors('password')} ? 'is-invalid' : ''" required />
<div class="password-strength">
<div class="strength-bar">
<div class="strength-bar-fill" id="strengthBar"></div>
<div class="strength-text" id="strengthText"></div>
<div th:if="${#fields.hasErrors('password')}" class="form-error" th:errors="*{password}">Error</div>
</div>

<div class="col-md-6">
<div class="form-group">
<label class="form-label" for="phone">Phone Number</label>
<input type="tel" id="phone" th:field="*{phone}" class="form-input" placeholder="+1 234 567 890" th:classappend="${#fields.hasErrors('phone')} ? 'is-invalid' : ''" />
<div th:if="${#fields.hasErrors('phone')}" class="form-error" th:errors="*{phone}">Error</div>
</div>

<div class="form-group">
<label class="form-check">
<input type="checkbox" required />
<span class="form-check-label">I agree to the <a href="#" style="color: var(--color-primary);">Terms of Service</a> and <a href="#" style="color: var(--color-primary);">Privacy Policy</a></span>
</label>
</div>

<button type="submit" class="btn btn-primary-gradient btn-lg w-100"><i class="fas fa-user-graduate"></i> Create Student Account</button>
</form>

<div class="text-center mt-4">
<p style="color: var(--text-secondary); font-size: var(--font-size-sm);">Already have an account? <a th:href="@{/login}" style="color: var(--color-primary); font-weight: var(--font-weight-semibold);">Sign in</a></p>
<a th:href="@{/register}" class="btn btn-ghost btn-sm mt-2"><i class="fas fa-arrow-left me-1"></i> Choose different role</a>
</div>
</div>
</div>

<div th:replace="~{fragments/header::scripts}"></div>
</body>
</html>
"""

with open('src/main/resources/templates/auth/student-register.html', 'w', encoding='utf-8') as f:
    f.write(student_html)

print(f"Student OK - {len(student_html)} bytes")

employer_html = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security" lang="
