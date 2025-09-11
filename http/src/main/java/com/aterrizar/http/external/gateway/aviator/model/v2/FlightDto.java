package com.aterrizar.http.external.gateway.aviator.model.v2;

public record FlightDto(String flightNumber, String price, AirportDto from, AirportDto to) {}
