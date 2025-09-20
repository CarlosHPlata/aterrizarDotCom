package com.aterrizar.service.checkin.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configuration properties for Digital Visa feature. 
 * Bean para leer la propiedad feature.digital.visa.enabled-countries del application.properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "feature.digital.visa")
public class DigitalVisaProperties {

    /**
     * List of country codes that require digital visa validation. Default: IN (India), AU
     * (Australia)
     */
    private List<String> enabledCountries = List.of("IN", "AU");

    /**
     * Checks if a country requires digital visa validation.
     *
     * @param countryCode the country code to check
     * @return true if the country requires digital visa validation
     */
    public boolean isDigitalVisaRequired(String countryCode) {
        return enabledCountries.contains(countryCode);
    }

    /**
     * Gets the list of enabled countries for digital visa validation.
     *
     * @return list of country codes
     */
    public List<String> getEnabledCountries() {
        return List.copyOf(enabledCountries);
    }
}
