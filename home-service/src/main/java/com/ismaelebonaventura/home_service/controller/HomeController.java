package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.HomeResponse;
import com.ismaelebonaventura.home_service.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/homes")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public List<HomeResponse> getAllHomes() {
        return homeService.getAllHomes();
    }

    @GetMapping("/{homeId}")
    public HomeResponse getHome(@PathVariable Integer homeId) {
        return homeService.getHome(homeId);
    }
}