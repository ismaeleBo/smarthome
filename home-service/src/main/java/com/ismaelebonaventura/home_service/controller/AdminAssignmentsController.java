package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.UserAssignmentResponse;
import com.ismaelebonaventura.home_service.dto.UserHomeAssignmentsResponse;
import com.ismaelebonaventura.home_service.service.AdminAssignmentsService;
import com.ismaelebonaventura.home_service.service.UserAssignmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/assignments")
@RequiredArgsConstructor
public class AdminAssignmentsController {

    private final AdminAssignmentsService adminAssignmentsService;
    private final UserAssignmentsService userAssignmentsService;

    @GetMapping("/users")
    public UserHomeAssignmentsResponse getUserAssignments() {
        return adminAssignmentsService.getUserAssignments();
    }

    @GetMapping("/users/{userId}")
    public UserAssignmentResponse getUserAssignment(@PathVariable UUID userId) {
        return userAssignmentsService.getAssignment(userId);
    }
}