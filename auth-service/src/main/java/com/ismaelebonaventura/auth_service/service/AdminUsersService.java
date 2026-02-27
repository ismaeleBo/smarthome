package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.dto.UserSummaryResponse;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUsersService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private UserSummaryResponse toSummary(User u) {
        return new UserSummaryResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getBirthDate(),
                u.getRole(),
                u.getStatus()
        );
    }
}