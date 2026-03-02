package com.ismaelebonaventura.home_service.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EVENTS_EXCHANGE = "smarthome.events";

    public static final String RK_MEMBER_REGISTERED = "auth.member.registered";
    public static final String RK_MEMBER_INVITATION_ACCEPTED = "auth.member.invitation.accepted";

    public static final String HOME_MEMBER_REGISTERED_QUEUE = "home.member.registered.events";
    public static final String HOME_MEMBER_INVITATION_ACCEPTED_QUEUE = "home.member.invitation.accepted.events";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue homeMemberRegisteredQueue() {
        return QueueBuilder.durable(HOME_MEMBER_REGISTERED_QUEUE).build();
    }

    @Bean
    public Binding bindMemberRegistered(Queue homeMemberRegisteredQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(homeMemberRegisteredQueue)
                .to(eventsExchange)
                .with(RK_MEMBER_REGISTERED);
    }

    @Bean
    public Queue homeMemberInvitationAcceptedQueue() {
        return QueueBuilder.durable(HOME_MEMBER_INVITATION_ACCEPTED_QUEUE).build();
    }

    @Bean
    public Binding bindMemberInvitationAccepted(Queue homeMemberInvitationAcceptedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(homeMemberInvitationAcceptedQueue)
                .to(eventsExchange)
                .with(RK_MEMBER_INVITATION_ACCEPTED);
    }
}