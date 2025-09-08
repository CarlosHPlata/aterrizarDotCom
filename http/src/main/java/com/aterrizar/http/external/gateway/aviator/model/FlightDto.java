package com.aterrizar.http.external.gateway.aviator.model;

public record FlightDto(String flightNumber, long price, AirportDto from, AirportDto to) {}
