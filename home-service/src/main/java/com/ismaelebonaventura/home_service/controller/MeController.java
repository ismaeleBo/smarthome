package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.HomeResponse;
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
    public List<HomeResponse> getMyHomes() {

        UUID userId = (UUID) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        var headHomes = homeRepository.findAllByHeadUserId(userId).stream()
                .map(h -> new HomeResponse(h.getHomeId(), h.getStatus(), h.getAddress(), h.getStreetNumber(), h.getCity(), h.getPricePerKwh(), h.getHeadUserId()))
                .toList();

        var memberHomeIds = homeMemberRepository.findHomeIdsByMemberUserId(userId);
        var memberHomes = memberHomeIds.stream()
                .map(homeId -> homeRepository.findByHomeId(homeId)
                        .map(h -> new HomeResponse(h.getHomeId(), h.getStatus(), h.getAddress(), h.getStreetNumber(), h.getCity(), h.getPricePerKwh(), h.getHeadUserId()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        var analystHomeIds = analystHomeRepository.findHomeIdsByAnalystUserId(userId);
        var analystHomes = analystHomeIds.stream()
                .map(homeId -> homeRepository.findByHomeId(homeId)
                        .map(h -> new HomeResponse(h.getHomeId(), h.getStatus(), h.getAddress(), h.getStreetNumber(), h.getCity(), h.getPricePerKwh(), h.getHeadUserId()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return java.util.stream.Stream.of(headHomes, memberHomes, analystHomes)
                .flatMap(List::stream)
                .filter(r -> r.status() != HomeStatus.DISABLED)
                .collect(java.util.stream.Collectors.toMap(
                        HomeResponse::homeId,
                        r -> r,
                        (a, b) -> a
                ))
                .values().stream().toList();
    }
}