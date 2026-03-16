package com.aterrizar.http.external.gateway.scanner;

import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import org.springframework.stereotype.Service;

import com.aterrizar.service.external.scanner.ScanValidationStatus;
import com.aterrizar.service.external.scanner.ScannerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JumioGatewayAdapter implements ScannerGateway {

    private final JumioHttpClient client;

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
        return "JU-";
    }
}