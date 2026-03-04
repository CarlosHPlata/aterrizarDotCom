package com.aterrizar.http.external.gateway.biometric;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.biometric.model.BiometricStartDto;
import com.aterrizar.http.external.gateway.biometric.model.BiometricVerifyRequestDto;
import com.aterrizar.http.external.gateway.biometric.model.BiometricVerifyResponseDto;

@BaseUrl("${http.client.biometric.base.url}")
@HttpExchange(value = "v1/", accept = "application/json", contentType = "application/json")
public interface BiometricHttpClient {

    @GetExchange("start")
    BiometricStartDto start();

    @PostExchange("verify")
    BiometricVerifyResponseDto verify(@RequestBody BiometricVerifyRequestDto request);
}
