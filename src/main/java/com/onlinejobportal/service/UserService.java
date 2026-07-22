package com.onlinejobportal.service;

import com.onlinejobportal.dto.auth.ChangePasswordRequest;
import com.onlinejobportal.dto.auth.RegisterRequest;
import com.onlinejobportal.dto.employer.EmployerProfileRequest;
import com.onlinejobportal.dto.employer.EmployerProfileResponse;
import com.onlinejobportal.dto.student.StudentProfileRequest;
import com.onlinejobportal.dto.student.StudentProfileResponse;
import com.onlinejobportal.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User registerUser(RegisterRequest request);

    User registerStudent(RegisterRequest request);

    User registerEmployer(RegisterRequest request);

    StudentProfileResponse getStudentProfile(Long userId);

    StudentProfileResponse updateStudentProfile(Long userId, StudentProfileRequest request);

    EmployerProfileResponse getEmployerProfile(Long userId);

    EmployerProfileResponse updateEmployerProfile(Long userId, EmployerProfileRequest request);

    User getUserById(Long userId);

    User getUserByEmail(String email);

    Page<User> getAllUsers(int page, int size);

    Page<User> searchUsers(String keyword, int page, int size);

    Page<User> getUsersByRole(String roleName, int page, int size);

    Page<User> searchUsersByRole(String roleName, String keyword, int page, int size);

    void toggleUserEnabled(Long userId);

    void deleteUser(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);

    boolean existsByEmail(String email);

    long getTotalUserCount();

    long getUsersCountByRole(String roleName);

    long getNewUsersToday();
}

