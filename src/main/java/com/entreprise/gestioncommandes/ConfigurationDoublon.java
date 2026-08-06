package com.entreprise.gestioncommandes;
import com.entreprise.gestioncommandes.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationDoublon {

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }
}
