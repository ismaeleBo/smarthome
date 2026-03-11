package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.UserHomesResponse;
import com.ismaelebonaventura.home_service.service.UserHomesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserHomesController {

    private final UserHomesService userHomesService;

    @GetMapping("/{userId}/homes")
    public UserHomesResponse getUserHomes(@PathVariable UUID userId) {
        return userHomesService.getUserHomes(userId);
    }
}