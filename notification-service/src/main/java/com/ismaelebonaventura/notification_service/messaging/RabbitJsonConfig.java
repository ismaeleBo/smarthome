package com.ismaelebonaventura.notification_service.messaging;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitJsonConfig {

    @Bean
    public JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        var mapper = new org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper();
        mapper.setTypePrecedence(
                org.springframework.amqp.support.converter.JacksonJavaTypeMapper.TypePrecedence.INFERRED
        );

        converter.setJavaTypeMapper(mapper);
        return converter;
    }
}