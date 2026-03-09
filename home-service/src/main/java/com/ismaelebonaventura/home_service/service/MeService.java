package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.dto.HomeResponse;
import com.ismaelebonaventura.home_service.model.HomeStatus;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MeService {

	private final HomeRepository homeRepository;
	private final HomeMemberRepository homeMemberRepository;
	private final AnalystHomeRepository analystHomeRepository;

	@Transactional(readOnly = true)
	public List<HomeResponse> getMyHomes(UUID userId) {
		var headHomes = homeRepository.findAllByHeadUserId(userId).stream()
				.map(this::toResponse)
				.toList();

		var memberHomeIds = homeMemberRepository.findHomeIdsByMemberUserId(userId);
		var memberHomes = memberHomeIds.stream()
				.map(homeId -> homeRepository.findByHomeId(homeId)
						.map(this::toResponse)
						.orElse(null))
				.filter(Objects::nonNull)
				.toList();

		var analystHomeIds = analystHomeRepository.findHomeIdsByAnalystUserId(userId);
		var analystHomes = analystHomeIds.stream()
				.map(homeId -> homeRepository.findByHomeId(homeId)
						.map(this::toResponse)
						.orElse(null))
				.filter(Objects::nonNull)
				.toList();

		return Stream.of(headHomes, memberHomes, analystHomes)
				.flatMap(List::stream)
				.filter(r -> r.status() != HomeStatus.DISABLED)
				.collect(Collectors.toMap(
						HomeResponse::homeId,
						r -> r,
						(a, b) -> a))
				.values().stream().toList();
	}

	private HomeResponse toResponse(com.ismaelebonaventura.home_service.model.Home home) {
		return new HomeResponse(
				home.getHomeId(),
				home.getStatus(),
				home.getAddress(),
				home.getStreetNumber(),
				home.getCity(),
				home.getPricePerKwh(),
				home.getHeadUserId());
	}
}
