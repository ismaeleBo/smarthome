package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.aop.Audited;
import com.ismaelebonaventura.home_service.dto.HomeResponse;
import com.ismaelebonaventura.home_service.exception.ConflictException;
import com.ismaelebonaventura.home_service.model.Home;
import com.ismaelebonaventura.home_service.model.HomeStatus;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository repository;

    @Transactional(readOnly = true)
    public List<HomeResponse> getAllHomes() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HomeResponse getHome(Integer homeId) {
        Home home = repository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        return toResponse(home);
    }

    @Audited("CONFIGURE_HOME")
    @Transactional
    public void configureHome(Integer homeId,
                              String address,
                              String streetNumber,
                              String city,
                              Double pricePerKwh) {

        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }

        if (pricePerKwh == null || pricePerKwh <= 0) {
            throw new IllegalArgumentException("Price per kWh must be positive");
        }

        Home home = repository.findByHomeId(homeId)
                .orElseGet(() -> new Home(homeId));

        home.configure(address, streetNumber, city, pricePerKwh);

        repository.save(home);
    }

    @Audited("DISABLE_HOME")
    @Transactional
    public void disableHome(Integer homeId) {

        Home home = repository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        home.disable();
    }

    @Transactional
    public void reactivateHome(Integer homeId) {

        Home home = repository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        home.reactivate();
    }

    @Audited("ASSIGN_HEAD")
    @Transactional
    public void assignHead(Integer homeId, UUID headUserId) {

        if (headUserId == null) {
            throw new IllegalArgumentException("headUserId is required");
        }

        Home home = repository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        if (home.getStatus() != HomeStatus.CONFIGURED) {
            throw new ConflictException("Home must be CONFIGURED to assign a Head");
        }

        if (home.getHeadUserId() != null) {
            throw new ConflictException("Home already has a Head assigned");
        }

        home.setHeadUserId(headUserId);
    }

    private HomeResponse toResponse(Home home) {
        return new HomeResponse(
                home.getHomeId(),
                home.getStatus(),
                home.getAddress(),
                home.getStreetNumber(),
                home.getCity(),
                home.getPricePerKwh(),
                home.getHeadUserId()
        );
    }
}
