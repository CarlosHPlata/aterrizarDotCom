package com.aterrizar.service.external.scanner;

public interface ScannerGateway {
    /**
     * Fetches a scan token for the given country code.
     *
     * @return a token string prefixed with the provider identifier
     */
    String getToken();

    /**
     * Validates a document against the scanner provider.
     *
     * @param token the scan token previously issued
     * @param documentId the document ID provided by the frontend (12 characters, provider-prefixed)
     * @return the validation result status
     */
    ScanValidationStatus validate(String token, String documentId);

    String getDocumentPrefix();
}
