package com.aterrizar.service.external;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;
import com.neovisionaries.i18n.CountryCode;

@Service
public class MockFlightGateway implements FlightGateway {

  @Override
  public List<FlightData> getFlightData(List<String> flightNumbers) {
    return flightNumbers.stream()
        .map(
            flightNumber ->
                new FlightData(
                    flightNumber,
                    30000L,
                    new Airport(CountryCode.MX, "MEX"),
                    new Airport(CountryCode.SV, "SJD")))
        .toList();
  }
}
