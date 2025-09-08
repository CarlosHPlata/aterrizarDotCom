package com.aterrizar.http.external.gateway.aviator;

import java.util.List;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.aviator.model.FlightDto;

@BaseUrl("${http.client.aviator.base.url}")
@HttpExchange(value = "v1/", accept = "application/json", contentType = "application/json")
interface AviatorHttpClient {
    @GetExchange("flights")
    public List<FlightDto> getFlights();
}
