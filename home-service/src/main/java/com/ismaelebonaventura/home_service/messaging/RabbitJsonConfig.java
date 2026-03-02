package com.ismaelebonaventura.home_service.messaging;

import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitJsonConfig {

    @Bean
    public JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        DefaultJacksonJavaTypeMapper mapper = new DefaultJacksonJavaTypeMapper();
        mapper.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);

        mapper.setTrustedPackages(
                "com.ismaelebonaventura.*",
                "java.util",
                "java.time"
        );

        converter.setJavaTypeMapper(mapper);
        return converter;
    }
}