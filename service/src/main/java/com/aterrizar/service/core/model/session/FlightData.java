package com.aterrizar.service.core.model.session;

import java.io.Serializable;

import lombok.Builder;

@Builder
public record FlightData(String flightNumber, long price, Airport departure, Airport destination)
    implements Serializable {}
