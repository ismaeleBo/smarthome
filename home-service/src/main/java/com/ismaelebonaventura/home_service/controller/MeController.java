package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.HomeResponse;
import com.ismaelebonaventura.home_service.service.MeService;
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

	private final MeService meService;

	@GetMapping("/homes")
	public List<HomeResponse> getMyHomes() {

		UUID userId = (UUID) Objects.requireNonNull(SecurityContextHolder.getContext()
				.getAuthentication()).getPrincipal();

		return meService.getMyHomes(userId);
	}
}