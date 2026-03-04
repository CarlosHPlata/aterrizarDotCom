package com.aterrizar.http.external.gateway.scanner;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import com.aterrizar.service.external.ScannerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScannerGatewayAdapter implements ScannerGateway {

    private final ScannerHttpClient httpClient;

    @Override
    public String generateToken(String provider) {
        return httpClient.getToken(provider).token();
    }

    @Override
    public String validateDocument(String token, String documentId) {
        ValidateRequest request = new ValidateRequest(token, documentId);
        return httpClient.validateDocument(request).status();
    }
}
