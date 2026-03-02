package com.ismaelebonaventura.auth_service.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EVENTS_EXCHANGE = "smarthome.events";

    public static final String AUTH_INVITATION_QUEUE = "auth.invitation.events";

    public static final String NOTIFICATION_AUTH_ACTIVATION_QUEUE = "notification.auth.activation.events";
    public static final String AUTH_ACTIVATION_ROUTING_KEY = "auth.activation.created";
    public static final String AUTH_MEMBER_REGISTERED_ROUTING_KEY = "auth.member.registered";
    public static final String AUTH_MEMBER_INVITATION_ACCEPTED_ROUTING_KEY = "auth.member.invitation.accepted";
    
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue authInvitationQueue() {
        return QueueBuilder.durable(AUTH_INVITATION_QUEUE).build();
    }

    @Bean
    public Binding bindInvitationEvents(Queue authInvitationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(authInvitationQueue)
                .to(eventsExchange)
                .with("invitation.*");
    }

    @Bean
    public Queue notificationAuthActivationQueue() {
        return QueueBuilder.durable(NOTIFICATION_AUTH_ACTIVATION_QUEUE).build();
    }

    @Bean
    public Binding bindAuthActivationEvents(Queue notificationAuthActivationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationAuthActivationQueue)
                .to(eventsExchange)
                .with(AUTH_ACTIVATION_ROUTING_KEY);
    }
}
