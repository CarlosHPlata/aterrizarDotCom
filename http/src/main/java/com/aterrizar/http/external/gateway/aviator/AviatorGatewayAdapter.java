package com.aterrizar.http.external.gateway.aviator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.aviator.model.AirportDto;
import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;
import com.aterrizar.service.external.FlightGateway;
import com.neovisionaries.i18n.CountryCode;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AviatorGatewayAdapter implements FlightGateway {
    private final AviatorHttpClient client;

    @Override
    public List<FlightData> getFlightData(List<String> flightNumbers) {
        return this.client.getFlights().stream()
                .map(
                        flightDto ->
                                FlightData.builder()
                                        .flightNumber(flightDto.flightNumber())
                                        .price(flightDto.price())
                                        .departure(mapToAirport(flightDto.from()))
                                        .destination(mapToAirport(flightDto.to()))
                                        .build())
                .toList();
    }

    private Airport mapToAirport(AirportDto airportDto) {
        return Airport.builder()
                .countryCode(CountryCode.getByCode(airportDto.country()))
                .airportCode(airportDto.name())
                .build();
    }
}
