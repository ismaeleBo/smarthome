package com.ismaelebonaventura.home_service.messaging;

import com.ismaelebonaventura.home_service.messaging.events.MemberInvitationAcceptedEvent;
import com.ismaelebonaventura.home_service.service.MemberAssociationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberInvitationAcceptedListener {

    private final MemberAssociationService memberAssociationService;

    @RabbitListener(queues = RabbitConfig.HOME_MEMBER_INVITATION_ACCEPTED_QUEUE)
    public void onAccepted(MemberInvitationAcceptedEvent event) {

        if (event == null || event.memberUserId() == null || event.homeId() == null || event.token() == null) {
            log.warn("Skipping invalid MemberInvitationAcceptedEvent: {}", event);
            return;
        }

        memberAssociationService.associateMemberToHome(
                event.memberUserId(),
                event.homeId(),
                event.token()
        );

        log.info("Member accepted invitation userId={} homeId={}", event.memberUserId(), event.homeId());
    }
}
