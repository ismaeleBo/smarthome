package com.ismaelebonaventura.home_service.messaging;

public final class RabbitConstants {
    private RabbitConstants() {}

    public static final String EVENTS_EXCHANGE = "smarthome.events";

    public static final String RK_INVITATION_CREATED = "invitation.created";
    public static final String RK_INVITATION_REVOKED = "invitation.revoked";
}