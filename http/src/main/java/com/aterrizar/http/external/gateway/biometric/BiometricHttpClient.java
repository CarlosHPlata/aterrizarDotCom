package com.aterrizar.http.external.gateway.biometric;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.biometric.model.v1.BiometricStartDto;
import com.aterrizar.http.external.gateway.biometric.model.v1.BiometricVerifyDto;
import com.aterrizar.http.external.gateway.biometric.model.v1.BiometricVerifyRequestDto;

@BaseUrl("${http.client.biometric.base.url}")
@HttpExchange(value = "biometric-service/v1/", accept = "application/json", contentType = "application/json")
public interface BiometricHttpClient {
    @GetExchange("start")
    BiometricStartDto startSession();

    @PostExchange("verify")
    BiometricVerifyDto verifySession(BiometricVerifyRequestDto request);
}
