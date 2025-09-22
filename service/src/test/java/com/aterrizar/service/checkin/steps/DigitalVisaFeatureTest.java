package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.aterrizar.service.checkin.feature.DigitalVisaFeature;

class DigitalVisaFeatureTest {

    @Test
    void shouldDefineContractForDigitalVisaAvailability() {
        // Test with a simple implementation
        DigitalVisaFeature feature = new TestDigitalVisaFeature();

        assertTrue(feature.isCountryAvailable("IN"));
        assertTrue(feature.isCountryAvailable("AU"));
        assertFalse(feature.isCountryAvailable("US"));
        assertFalse(feature.isCountryAvailable("GB"));
    }

    /** Simple test implementation of DigitalVisaFeature for testing purposes. */
    private static class TestDigitalVisaFeature implements DigitalVisaFeature {
        @Override
        public boolean isCountryAvailable(String countryCode) {
            return "IN".equals(countryCode) || "AU".equals(countryCode);
        }
    }
}
