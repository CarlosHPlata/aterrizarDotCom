package com.aterrizar.http.external.gateway.scanner;

import com.aterrizar.service.core.model.ValidationStatus;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import com.aterrizar.service.external.ScannerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScannerGatewayAdapter implements ScannerGateway {
    private final ScannerHttpClient httpClient;
    private final DocumentValidator documentValidator;

    @Override
    public String generateToken(String provider) {
        return httpClient.getToken(provider).token();
    }

    @Override
    public ValidationStatus validateDocument(String token, String documentId, CountryCode countryCode) {
        return documentValidator.validate(token, documentId, countryCode);
    }
}
