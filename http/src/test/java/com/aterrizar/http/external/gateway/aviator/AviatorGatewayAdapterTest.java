package com.aterrizar.http.external.gateway.aviator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.aterrizar.http.external.gateway.aviator.model.v2.AirportDto;
import com.aterrizar.http.external.gateway.aviator.model.v2.FlightDto;
import com.aterrizar.http.external.repositories.redis.flight.model.RedisFlight;
import com.aterrizar.http.external.repositories.redis.flight.repository.FlightRepository;
import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;

class AviatorGatewayAdapterTest {

    @Test
    void shouldReturnCachedFlightWithoutCallingHttpClient() {
        var client = org.mockito.Mockito.mock(AviatorV2HttpClient.class);
        var flightRepository = org.mockito.Mockito.mock(FlightRepository.class);
        var cachedFlight = buildFlightData("USJFKGBLHR");
        when(flightRepository.findById("USJFKGBLHR"))
                .thenReturn(Optional.of(new RedisFlight("USJFKGBLHR", cachedFlight)));

        var adapter = new AviatorGatewayAdapter(client, flightRepository);

        var response = adapter.getFlightData(List.of("USJFKGBLHR"));

        assertEquals(List.of(cachedFlight), response);
        verify(client, never()).getFlights("USJFKGBLHR");
    }

    @Test
    void shouldFetchAndCacheFlightWhenItDoesNotExistInRepository() {
        var client = org.mockito.Mockito.mock(AviatorV2HttpClient.class);
        var flightRepository = org.mockito.Mockito.mock(FlightRepository.class);
        var liveFlight = buildFlightDto("USJFKGBLHR");
        when(flightRepository.findById("USJFKGBLHR")).thenReturn(Optional.empty());
        when(client.getFlights("USJFKGBLHR")).thenReturn(liveFlight);

        var adapter = new AviatorGatewayAdapter(client, flightRepository);

        var response = adapter.getFlightData(List.of("USJFKGBLHR"));

        assertEquals(List.of(buildFlightData("USJFKGBLHR")), response);
        verify(client).getFlights("USJFKGBLHR");
        verify(flightRepository)
                .save(
                        argThat(
                                redisFlight ->
                                        redisFlight.getRedisId().equals("USJFKGBLHR")
                                                && redisFlight
                                                        .getFlightData()
                                                        .equals(buildFlightData("USJFKGBLHR"))));
    }

    private FlightDto buildFlightDto(String flightNumber) {
        return new FlightDto(
                flightNumber, "12345", new AirportDto("JFK", "US"), new AirportDto("LHR", "GB"));
    }

    private FlightData buildFlightData(String flightNumber) {
        return FlightData.builder()
                .flightNumber(flightNumber)
                .price(12345L)
                .departure(
                        Airport.builder()
                                .airportCode("JFK")
                                .countryCode(com.neovisionaries.i18n.CountryCode.US)
                                .build())
                .destination(
                        Airport.builder()
                                .airportCode("LHR")
                                .countryCode(com.neovisionaries.i18n.CountryCode.GB)
                                .build())
                .build();
    }
}
