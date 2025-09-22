package com.aterrizar.http.config.feature;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.DigitalVisaFeature;
import lombok.Data;

/** Configuration properties for Digital Visa feature. */
@Data
@Component
@ConfigurationProperties(prefix = "feature.digital.visa")
public class DigitalVisaProperties implements DigitalVisaFeature {

    /**
     * List of country codes that require digital visa validation. Default: IN (India), AU
     * (Australia)
     */
    private List<String> enabledCountries;

    /**
     * Checks if a country requires digital visa validation.
     *
     * @param countryCode the country code to check
     * @return true if the country requires digital visa validation
     */
    public boolean isCountryAvailable(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
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
