package com.ismaelebonaventura.home_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LogNotificationPublisher implements NotificationPublisher {

    @Override
    public void publishMemberInvitationEmail(String email, String token, LocalDateTime expiresAt) {
        log.info("EVENT member.invitation.created email={} token={} expiresAt={}", email, token, expiresAt);
    }
}