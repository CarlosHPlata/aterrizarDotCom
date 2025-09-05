package com.aterrizar.service.external;

import java.util.List;

import com.aterrizar.service.core.model.session.FlightData;

public interface FlightGateway {
  List<FlightData> getFlightData(List<String> flightNumbers);
}
