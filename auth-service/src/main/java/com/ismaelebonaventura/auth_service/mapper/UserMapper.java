package com.ismaelebonaventura.auth_service.mapper;

import com.ismaelebonaventura.auth_service.dto.UserSummaryResponse;
import com.ismaelebonaventura.auth_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryResponse toSummary(User u) {
        if (u == null) return null;

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