package com.aterrizar.http.external.gateway.aviator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.aviator.model.v2.AirportDto;
import com.aterrizar.http.external.gateway.aviator.model.v2.FlightDto;
import com.aterrizar.http.external.repositories.redis.flight.model.RedisFlight;
import com.aterrizar.http.external.repositories.redis.flight.repository.FlightRepository;
import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;
import com.aterrizar.service.external.FlightGateway;
import com.neovisionaries.i18n.CountryCode;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AviatorGatewayAdapter implements FlightGateway {
    private final AviatorV2HttpClient client;
    private final FlightRepository flightRepository;

    @Override
    public List<FlightData> getFlightData(List<String> flightNumbers) {
        return flightNumbers.stream().map(this::getFlightData).toList();
    }

    private FlightData getFlightData(String flightNumber) {
        return flightRepository
                .findById(flightNumber)
                .map(RedisFlight::getFlightData)
                .orElseGet(() -> fetchAndCacheFlightData(flightNumber));
    }

    private FlightData fetchAndCacheFlightData(String flightNumber) {
        var flightData = mapToFlightData(client.getFlights(flightNumber));
        flightRepository.save(new RedisFlight(flightNumber, flightData));
        return flightData;
    }

    private FlightData mapToFlightData(FlightDto flightDto) {
        return FlightData.builder()
                .flightNumber(flightDto.flightNumber())
                .price(Long.parseLong(flightDto.price()))
                .departure(mapToAirport(flightDto.from()))
                .destination(mapToAirport(flightDto.to()))
                .build();
    }

    private Airport mapToAirport(AirportDto airportDto) {
        return Airport.builder()
                .countryCode(CountryCode.getByCode(airportDto.country()))
                .airportCode(airportDto.airport())
                .build();
    }
}
