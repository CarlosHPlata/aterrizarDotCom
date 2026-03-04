package com.aterrizar.http.external.gateway;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.aterrizar.http.config.BaseUrl;

@BaseUrl("${external.payment-service.url:http://localhost:3001}")
@HttpExchange("/payment-service/v1")
public interface PaymentStatusClient {

    // Cumple con: GET /payment-service/v1/status/{token} always returns {"status": "SUCCESS"}
    @GetExchange("/status/{token}")
    Map<String, String> getPaymentStatus(@PathVariable("token") String token);
}
