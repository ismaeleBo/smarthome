package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.AssignAnalystHomesRequest;
import com.ismaelebonaventura.home_service.dto.AssignHeadRequest;
import com.ismaelebonaventura.home_service.dto.ConfigureHomeRequest;
import com.ismaelebonaventura.home_service.service.AnalystAccessService;
import com.ismaelebonaventura.home_service.service.HomeService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/homes")
@RequiredArgsConstructor
public class HomeAdminController {

    private final HomeService homeService;
    private final AnalystAccessService analystAccessService;

    @PostMapping("/{homeId}/configure")
    public ResponseEntity<Void> configureHome(
            @PathVariable Integer homeId,
            @RequestBody ConfigureHomeRequest request
    ) {
        homeService.configureHome(
                homeId,
                request.address(),
                request.streetNumber(),
                request.city(),
                request.pricePerKwh()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{homeId}/disable")
    public ResponseEntity<Void> disableHome(
            @PathVariable Integer homeId
    ) {
        homeService.disableHome(homeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{homeId}/reactivate")
    public ResponseEntity<Void> reactivateHome(
            @PathVariable Integer homeId
    ) {
        homeService.reactivateHome(homeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{homeId}/head")
    public ResponseEntity<Void> assignHead(
            @PathVariable Integer homeId,
            @RequestBody AssignHeadRequest request
    ) {
        homeService.assignHead(homeId, request.headUserId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/analysts/assign")
    public ResponseEntity<Void> assignAnalystHomes(@RequestBody AssignAnalystHomesRequest request) {

        analystAccessService.assignHomes(request.analystUserId(), request.homeIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analysts/{analystUserId}/homes")
    public ResponseEntity<List<Integer>> getAssignedHomes(@PathVariable String analystUserId) {
        List<Integer> homeIds = analystAccessService.getAssignedHomeIds(java.util.UUID.fromString(analystUserId));
        return ResponseEntity.ok(homeIds);
    }
}