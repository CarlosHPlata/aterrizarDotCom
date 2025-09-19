package com.aterrizar.service.checkin.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@Data
@ConfigurationProperties(prefix = "feature.health.declaration.enabled")
@NoArgsConstructor
public class HealthDeclarationProperties {
    private List<CountryCode> countries;

    public HealthDeclarationProperties(CountryCode... countries) {
        this(List.of(countries));
    }

    public HealthDeclarationProperties(List<CountryCode> countries) {
        this.countries = countries;
    }
}
