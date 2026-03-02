package com.ismaelebonaventura.notification_service.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "smarthome.events";

    // activation
    public static final String RK_ACTIVATION = "auth.activation.created";
    public static final String QUEUE_ACTIVATION = "notification.auth.activation.events";

    // invitations
    public static final String RK_INVITATION_ALL = "invitation.*";
    public static final String QUEUE_INVITATIONS = "notification.invitation.events";

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue activationQueue() {
        return QueueBuilder.durable(QUEUE_ACTIVATION).build();
    }

    @Bean
    public Binding activationBinding(Queue activationQueue, TopicExchange notificationsExchange) {
        return BindingBuilder.bind(activationQueue)
                .to(notificationsExchange)
                .with(RK_ACTIVATION);
    }

    @Bean
    public Queue invitationsQueue() {
        return QueueBuilder.durable(QUEUE_INVITATIONS).build();
    }

    @Bean
    public Binding invitationsBinding(Queue invitationsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder.bind(invitationsQueue)
                .to(notificationsExchange)
                .with(RK_INVITATION_ALL);
    }
}