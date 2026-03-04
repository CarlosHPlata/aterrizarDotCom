package com.aterrizar.http.external.gateway.biometric;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.aterrizar.http.external.gateway.biometric.model.BiometricVerifyRequestDto;
import com.aterrizar.service.core.model.biometric.BiometricStart;
import com.aterrizar.service.external.BiometricGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BiometricGatewayAdapter implements BiometricGateway {

    private final BiometricHttpClient biometricHttpClient;

    @Override
    public BiometricStart start() {
        var dto = biometricHttpClient.start();
        return BiometricStart.builder().authSessionToken(dto.authSessionToken()).build();
    }

    @Override
    public boolean verify(String signedToken) {
        try {
            var response = biometricHttpClient.verify(new BiometricVerifyRequestDto(signedToken));
            return response.verified();
        } catch (WebClientResponseException.NotAcceptable e) {
            return false;
        }
    }
}
