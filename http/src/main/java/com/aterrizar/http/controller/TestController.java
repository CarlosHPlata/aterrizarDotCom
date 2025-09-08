package com.aterrizar.http.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aterrizar.http.external.gateway.aviator.AviatorGatewayAdapter;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TestController {
  private final AviatorGatewayAdapter aviatorHttpClient;

  @GetMapping("/test")
  public String testEndpoint() {
    var flights = aviatorHttpClient.getFlights();
    System.out.println(flights);

    return "Request made to Aviator service";
  }
}
