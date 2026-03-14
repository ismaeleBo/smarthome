package com.ismaelebonaventura.analytics_service.controller;

import com.ismaelebonaventura.analytics_service.dto.AnalystDashboardRequest;
import com.ismaelebonaventura.analytics_service.dto.AnalystDashboardResponse;
import com.ismaelebonaventura.analytics_service.dto.HomeDashboardResponse;
import com.ismaelebonaventura.analytics_service.service.AnalystDashboardService;
import com.ismaelebonaventura.analytics_service.service.CurrentUserService;
import com.ismaelebonaventura.analytics_service.service.HomeDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AnalyticsController {

    private final HomeDashboardService homeDashboardService;
    private final AnalystDashboardService analystDashboardService;
    private final CurrentUserService currentUserService;

    @GetMapping("/homes/{homeId}/dashboard")
    public HomeDashboardResponse getHomeDashboard(
            @PathVariable Integer homeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String applianceType) {
        return homeDashboardService.getDashboard(
                currentUserService.getCurrentUserId(),
                currentUserService.getCurrentRole(),
                homeId,
                from,
                to,
                applianceType);
    }

    @PostMapping("/analyst/dashboard")
    public AnalystDashboardResponse getAnalystDashboard(
            @RequestBody AnalystDashboardRequest request) {
        return analystDashboardService.getDashboard(
                currentUserService.getCurrentUserId(),
                currentUserService.getCurrentRole(),
                request.homeIds(),
                request.from(),
                request.to(),
                request.applianceType());
    }
}