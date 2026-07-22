package com.onlinejobportal.service;

import com.onlinejobportal.dto.dashboard.DashboardStatsResponse;

public interface DashboardService {

    DashboardStatsResponse getAdminDashboardStats();

    DashboardStatsResponse getEmployerDashboardStats(Long employerId);

    DashboardStatsResponse getJobSeekerDashboardStats(Long seekerId);
}

