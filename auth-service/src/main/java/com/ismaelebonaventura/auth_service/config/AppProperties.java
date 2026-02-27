package com.ismaelebonaventura.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Bootstrap bootstrap = new Bootstrap();

    @Getter
    @Setter
    public static class Bootstrap {
        private Admin admin = new Admin();
    }

    @Getter
    @Setter
    public static class Admin {
        private boolean enabled = false;
        private String email;
        private String password;
    }
}
