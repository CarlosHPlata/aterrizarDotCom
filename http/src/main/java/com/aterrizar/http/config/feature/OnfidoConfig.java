package com.aterrizar.http.config.feature;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.OnfidoFeature;

import lombok.Data;

/** Configuration properties for Onfido high-security identity verification feature. */
@Data
@Component
@ConfigurationProperties(prefix = "feature.onfido")
public class OnfidoConfig implements OnfidoFeature {
    /**
     * List of country codes that require Onfido (high-security) identity verification.
     * Default: US
     */
    private List<String> enabledCountries;

    /**
     * Checks if a country requires Onfido identity verification.
     *
     * @param countryCode the country code to check
     * @return true if the country requires Onfido
     */
    @Override
    public boolean isCountryAvailable(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
        return enabledCountries.contains(countryCode);
    }
}