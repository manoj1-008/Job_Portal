package com.onlinejobportal.service.impl;

import com.onlinejobportal.dto.auth.ChangePasswordRequest;
import com.onlinejobportal.dto.auth.RegisterRequest;
import com.onlinejobportal.dto.employer.EmployerProfileRequest;
import com.onlinejobportal.dto.employer.EmployerProfileResponse;
import com.onlinejobportal.dto.student.StudentProfileRequest;
import com.onlinejobportal.dto.student.StudentProfileResponse;
import com.onlinejobportal.entity.Role;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.BadRequestException;
import com.onlinejobportal.exception.DuplicateResourceException;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.repository.JobApplicationRepository;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.RoleRepository;
import com.onlinejobportal.repository.UserRepository;
import com.onlinejobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        // Database stores roles with ROLE_ prefix (e.g. ROLE_JOBSEEKER, ROLE_EMPLOYER)
        // but the registration form sends short names (e.g. JOB_SEEKER, EMPLOYER)
        String roleName = request.role().startsWith("ROLE_") ? request.role() : "ROLE_" + request.role();
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Invalid role: " + request.role()));

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .enabled(true)
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} with role {}", savedUser.getEmail(), role.getName());
        return savedUser;
    }

 @Override
public User registerStudent(RegisterRequest request) {
    if (!"ROLE_JOBSEEKER".equalsIgnoreCase(request.role())) {
        throw new BadRequestException("Student registration requires ROLE_JOBSEEKER role");
    }
    return registerUser(request);
}

@Override
public User registerEmployer(RegisterRequest request) {
    if (!"ROLE_EMPLOYER".equalsIgnoreCase(request.role())) {
        throw new BadRequestException("Employer registration requires ROLE_EMPLOYER role");
    }
    return registerUser(request);
}

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(Long userId) {
        User user = getUserById(userId);
        return mapToStudentProfile(user);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateStudentProfile(Long userId, StudentProfileRequest request) {
        User user = getUserById(userId);

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        User updatedUser = userRepository.save(user);
        log.info("Student profile updated for user ID: {}", userId);
        return mapToStudentProfile(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployerProfileResponse getEmployerProfile(Long userId) {
        User user = getUserById(userId);
        return mapToEmployerProfile(user);
    }

    @Override
    @Transactional
    public EmployerProfileResponse updateEmployerProfile(Long userId, EmployerProfileRequest request) {
        User user = getUserById(userId);

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        User updatedUser = userRepository.save(user);
        log.info("Employer profile updated for user ID: {}", userId);
        return mapToEmployerProfile(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.searchUsers(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersByRole(String roleName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findByRole_Name(roleName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsersByRole(String roleName, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.searchUsersByRole(roleName, keyword, pageable);
    }

    @Override
    @Transactional
    public void toggleUserEnabled(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        log.info("User ID: {} enabled status toggled to {}", userId, user.isEnabled());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
        log.info("User ID: {} deleted successfully", userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed for user ID: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUsersCountByRole(String roleName) {
        return userRepository.countByRole_Name(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public long getNewUsersToday() {
        return userRepository.countUsersCreatedToday();
    }

    private StudentProfileResponse mapToStudentProfile(User user) {
        return new StudentProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                null, // headline
                null, // summary
                null, // education
                null, // skills
                null, // experience
                null, // linkedinUrl
                null, // githubUrl
                user.getResume() != null ? user.getResume().getFileName() : null,
                user.getCreatedAt()
        );
    }

    private EmployerProfileResponse mapToEmployerProfile(User user) {
        long activeJobs = jobRepository.countByActiveTrueAndEmployer_Id(user.getId());
        long totalApplicants = applicationRepository.countByJob_Employer_Id(user.getId());

        return new EmployerProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getFullName(),
                null, // companyDescription
                null, // companyWebsite
                null, // companyLocation
                (int) activeJobs,
                (int) totalApplicants,
                user.getCreatedAt()
        );
    }
}
