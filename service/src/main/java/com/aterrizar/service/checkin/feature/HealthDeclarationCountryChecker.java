package com.aterrizar.service.checkin.feature;

import java.util.List;

import com.aterrizar.service.core.model.session.FlightData;
import com.neovisionaries.i18n.CountryCode;

public interface HealthDeclarationCountryChecker {
    boolean isCountryEnabled(CountryCode countryCode);

    default boolean hasFlightsRequiringHealthDeclaration(List<FlightData> flights) {
        return flights.stream()
                .anyMatch(flight -> isCountryEnabled(flight.destination().countryCode()));
    }
}
