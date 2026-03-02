package com.ismaelebonaventura.home_service.messaging;

import com.ismaelebonaventura.home_service.messaging.events.MemberRegisteredEvent;
import com.ismaelebonaventura.home_service.service.MemberAssociationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRegisteredListener {

    private final MemberAssociationService memberAssociationService;

    @RabbitListener(queues = RabbitConfig.HOME_MEMBER_REGISTERED_QUEUE)
    public void onMemberRegistered(MemberRegisteredEvent event) {

        if (event == null || event.userId() == null || event.homeId() == null || event.email() == null) {
            log.warn("Skipping invalid MemberRegisteredEvent: {}", event);
            return;
        }

        memberAssociationService.associateMemberToHome(
                event.userId(),
                event.homeId(),
                event.email()
        );

        log.info("Member associated userId={} homeId={}", event.userId(), event.homeId());
    }
}