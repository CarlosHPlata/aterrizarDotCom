package com.aterrizar.http.external.gateway.scanner;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import com.aterrizar.service.external.scanner.ScanValidationStatus;
import com.aterrizar.service.external.scanner.ScannerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnfidoGatewayAdapter implements ScannerGateway {

    private final OnfidoHttpClient client;

    @Override
    public String getToken() {
        return client.getToken().token();
    }

    @Override
    public ScanValidationStatus validate(String token, String documentId) {
        var response = client.validate(new ValidateRequest(token, documentId));
        return ScanValidationStatus.valueOf(response.status());
    }

    @Override
    public String getDocumentPrefix() {
        return "ON-";
    }
}