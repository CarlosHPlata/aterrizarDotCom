package com.aterrizar.http.external.gateway.biometric;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.aterrizar.http.external.gateway.biometric.model.v1.BiometricVerifyRequestDto;
import com.aterrizar.service.external.BiometricGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BiometricGatewayAdapter implements BiometricGateway {
    private final BiometricHttpClient biometricHttpClient;

    @Override
    public String startSession() {
        var dto = biometricHttpClient.startSession();
        return dto.authSessionToken();
    }

    @Override
    public boolean verifySession(String authSessionToken) {
        try {
            var result =
                    biometricHttpClient.verifySession(
                            new BiometricVerifyRequestDto(authSessionToken));
            return result.verified();
        } catch (WebClientResponseException.NotAcceptable e) {
            return false;
        }
    }
}
