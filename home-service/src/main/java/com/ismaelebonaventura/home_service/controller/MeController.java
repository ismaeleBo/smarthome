package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.UserHomeResponse;
import com.ismaelebonaventura.home_service.model.HomeStatus;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final AnalystHomeRepository analystHomeRepository;

    @GetMapping("/homes")
    public List<UserHomeResponse> getMyHomes() {

        UUID userId = (UUID) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        var headHomes = homeRepository.findAllByHeadUserId(userId).stream()
                .map(h -> new UserHomeResponse(h.getHomeId(), h.getStatus()))
                .toList();

        var memberHomeIds = homeMemberRepository.findHomeIdsByMemberUserId(userId); // List<Integer>
        var memberHomes = memberHomeIds.stream()
                .map(homeId -> homeRepository.findByHomeId(homeId)
                        .map(h -> new UserHomeResponse(h.getHomeId(), h.getStatus()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        var analystHomeIds = analystHomeRepository.findHomeIdsByAnalystUserId(userId); // List<Integer>
        var analystHomes = analystHomeIds.stream()
                .map(homeId -> homeRepository.findByHomeId(homeId)
                        .map(h -> new UserHomeResponse(h.getHomeId(), h.getStatus()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return java.util.stream.Stream.of(headHomes, memberHomes, analystHomes)
                .flatMap(List::stream)
                .filter(r -> r.status() != HomeStatus.DISABLED)
                .collect(java.util.stream.Collectors.toMap(
                        UserHomeResponse::homeId,
                        r -> r,
                        (a, b) -> a
                ))
                .values().stream().toList();
    }
}