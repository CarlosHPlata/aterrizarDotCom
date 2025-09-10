package com.aterrizar.http.external.gateway.aviator;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.aviator.model.v2.FlightDto;

import jakarta.validation.constraints.Pattern;

@BaseUrl("${http.client.aviator.base.url}")
@HttpExchange(value = "v2/", accept = "application/json", contentType = "application/json")
public interface AviatorV2HttpClient {
    @GetExchange("flights/{flightId}")
    FlightDto getFlights(
            @PathVariable("flightId")
                    @Pattern(
                            regexp = "^[A-Za-z]{2}[A-Za-z]{3}[A-Za-z]{2}[A-Za-z]{3}$",
                            message = "Code must be 2+3+2+3 letters (e.g., USJFKGBLHR)")
                    String flightId);
}
