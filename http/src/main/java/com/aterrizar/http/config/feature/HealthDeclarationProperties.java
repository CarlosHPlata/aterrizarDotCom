package com.aterrizar.http.config.feature;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.aterrizar.service.checkin.feature.HealthDeclarationCountryChecker;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "feature.health.declaration.enabled")
public class HealthDeclarationProperties implements HealthDeclarationCountryChecker {
    private List<CountryCode> countries;

    public boolean isCountryEnabled(CountryCode countryCode) {
        return this.countries.contains(countryCode);
    }
}
