package com.aterrizar.service.external;

import com.aterrizar.service.core.model.session.FlightData;

import java.util.List;

public interface FlightGateway {
    List<FlightData> getFlightData(List<String> flightNumbers);
}
