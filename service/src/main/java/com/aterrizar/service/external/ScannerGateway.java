package com.aterrizar.service.external;

public interface ScannerGateway {
    String generateToken(String provider);

    String validateDocument(String token, String documentId);
}
