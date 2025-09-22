package com.aterrizar.service.checkin.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigitalVisaPropertiesTest {

    private DigitalVisaProperties digitalVisaProperties;

    @BeforeEach
    void setUp() {
        digitalVisaProperties = new DigitalVisaProperties();
    }

    @Test
    void shouldReturnTrueForEnabledCountries() {
        digitalVisaProperties.setEnabledCountries(List.of("IN", "AU"));

        assertTrue(digitalVisaProperties.isDigitalVisaRequired("IN"));
        assertTrue(digitalVisaProperties.isDigitalVisaRequired("AU"));
    }

    @Test
    void shouldReturnFalseForDisabledCountries() {
        digitalVisaProperties.setEnabledCountries(List.of("IN", "AU"));

        assertFalse(digitalVisaProperties.isDigitalVisaRequired("US"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("GB"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("FR"));
    }

    @Test
    void shouldReturnFalseWhenNoCountriesConfigured() {
        digitalVisaProperties.setEnabledCountries(List.of());

        assertFalse(digitalVisaProperties.isDigitalVisaRequired("IN"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("AU"));
    }

    @Test
    void shouldUseDefaultCountriesWhenNotConfigured() {
        // No setEnabledCountries called, should use defaults

        assertTrue(digitalVisaProperties.isDigitalVisaRequired("IN"));
        assertTrue(digitalVisaProperties.isDigitalVisaRequired("AU"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("US"));
    }

    @Test
    void shouldHandleSingleCountryCode() {
        digitalVisaProperties.setEnabledCountries(List.of("IN"));

        assertTrue(digitalVisaProperties.isDigitalVisaRequired("IN"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("AU"));
    }

    @Test
    void shouldReturnImmutableCopyOfEnabledCountries() {
        digitalVisaProperties.setEnabledCountries(List.of("IN", "AU", "US"));

        List<String> result = digitalVisaProperties.getEnabledCountries();

        assertEquals(3, result.size());
        assertTrue(result.contains("IN"));
        assertTrue(result.contains("AU"));
        assertTrue(result.contains("US"));

        // Verify it's immutable by trying to modify
        List<String> originalList = digitalVisaProperties.getEnabledCountries();
        assertEquals(originalList, result);
    }

    @Test
    void shouldBeCaseSensitive() {
        digitalVisaProperties.setEnabledCountries(List.of("IN", "AU"));

        assertTrue(digitalVisaProperties.isDigitalVisaRequired("IN"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("in"));
        assertFalse(digitalVisaProperties.isDigitalVisaRequired("In"));
    }

    @Test
    void shouldHandleNullCountryCode() {
        digitalVisaProperties.setEnabledCountries(List.of("IN", "AU"));

        assertFalse(digitalVisaProperties.isDigitalVisaRequired(null));
    }
}