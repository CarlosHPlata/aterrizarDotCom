package com.aterrizar.http.config.feature;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.EtaFeature;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "feature.homeoffice.eta")
public class EtaConfig implements EtaFeature {
    private List<String> enabledCountries;

    @Override
    public boolean isCountryAvailable(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
        return enabledCountries != null && enabledCountries.contains(countryCode);
    }

    public List<String> getEnabledCountries() {
        return enabledCountries == null ? List.of() : List.copyOf(enabledCountries);
    }
}
