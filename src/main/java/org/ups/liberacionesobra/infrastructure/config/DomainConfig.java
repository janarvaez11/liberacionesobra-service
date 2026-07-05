package org.ups.liberacionesobra.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ups.liberacionesobra.domain.service.SolicitudDomainService;

@Configuration
public class DomainConfig {

    @Bean
    SolicitudDomainService solicitudDomainService() {
        return new SolicitudDomainService();
    }
}
