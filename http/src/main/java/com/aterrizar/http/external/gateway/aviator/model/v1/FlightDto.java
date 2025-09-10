package com.aterrizar.http.external.gateway.aviator.model.v1;

public record FlightDto(String flightNumber, long price, AirportDto from, AirportDto to) {}
