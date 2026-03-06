package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.dto.UserSummaryResponse;
import com.ismaelebonaventura.auth_service.mapper.UserMapper;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUsersService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> userMapper.toSummary(u))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role)
                .stream()
                .map(u -> userMapper.toSummary(u))
                .toList();
    }
}