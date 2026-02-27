package com.ismaelebonaventura.auth_service.controller;

import com.ismaelebonaventura.auth_service.dto.UserSummaryResponse;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.service.AdminUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    @GetMapping
    public List<UserSummaryResponse> getAll() {
        return adminUsersService.getAllUsers();
    }

    @GetMapping("/heads")
    public List<UserSummaryResponse> getHeads() {
        return adminUsersService.getUsersByRole(Role.HEAD);
    }

    @GetMapping(params = "role")
    public List<UserSummaryResponse> getByRole(@RequestParam Role role) {
        return adminUsersService.getUsersByRole(role);
    }
}