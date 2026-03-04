package com.aterrizar.http.external.gateway.scanner;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.scanner.model.TokenResponse;
import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import com.aterrizar.http.external.gateway.scanner.model.ValidateResponse;

@BaseUrl("${http.client.scanner.base.url}")
@HttpExchange(value = "v1/", accept = "application/json", contentType = "application/json")
interface ScannerHttpClient {

    @GetExchange("{provider}/token")
    TokenResponse getToken(@PathVariable("provider") String provider);

    @PostExchange("validate")
    ValidateResponse validateDocument(@RequestBody ValidateRequest request);
}
