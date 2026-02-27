package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.model.AnalystHome;
import com.ismaelebonaventura.home_service.model.HomeStatus;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analyst")
@RequiredArgsConstructor
public class AnalystController {

    private final AnalystHomeRepository analystHomeRepository;
    private final HomeRepository homeRepository;

    @GetMapping("/homes")
    public List<Integer> getAssignedHomes() {

        UUID analystUserId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return analystHomeRepository.findByAnalystUserId(analystUserId)
                .stream()
                .map(AnalystHome::getHomeId)
                .distinct()
                .filter(homeId -> homeRepository.findByHomeId(homeId)
                        .map(h -> h.getStatus() != HomeStatus.DISABLED)
                        .orElse(false))
                .toList();
    }
}