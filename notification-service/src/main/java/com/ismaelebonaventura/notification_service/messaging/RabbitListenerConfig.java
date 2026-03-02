package com.ismaelebonaventura.notification_service.messaging;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        var converter = new JacksonJsonMessageConverter();

        var mapper = new org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper();
        mapper.setTypePrecedence(org.springframework.amqp.support.converter.JacksonJavaTypeMapper.TypePrecedence.INFERRED);

        mapper.setTrustedPackages("com.ismaelebonaventura.notification_service");

        converter.setJavaTypeMapper(mapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory jsonListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter jsonMessageConverter
    ) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rawListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new org.springframework.amqp.support.converter.SimpleMessageConverter());
        return factory;
    }
}
