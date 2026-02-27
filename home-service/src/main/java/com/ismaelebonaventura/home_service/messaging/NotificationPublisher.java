package com.ismaelebonaventura.home_service.messaging;

import java.time.LocalDateTime;

public interface NotificationPublisher {
    void publishMemberInvitationEmail(String email, String token, LocalDateTime expiresAt);
}