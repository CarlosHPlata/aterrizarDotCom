package com.aterrizar.service.checkin.scanner;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdScanProviderFactory {
    public static final String ONFIDO_PREFIX = "ON-";
    public static final String JUMIO_PREFIX = "JU-";

    private final List<String> highSecurityCountries;

    public IdScanProviderFactory(
            @Value("${feature.onfido.enabled.countries:US}") List<String> highSecurityCountries) {
        this.highSecurityCountries = highSecurityCountries;
    }

    public String getExpectedPrefix(String countryCode) {
        if (countryCode != null && highSecurityCountries.contains(countryCode.toUpperCase())) {
            return ONFIDO_PREFIX;
        }
        return JUMIO_PREFIX;
    }
}
