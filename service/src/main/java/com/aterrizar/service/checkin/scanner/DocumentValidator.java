package com.aterrizar.service.checkin.scanner;

import org.springframework.stereotype.Service;

import com.aterrizar.service.external.scanner.ScanValidationStatus;
import com.aterrizar.service.external.scanner.ScannerGateway;

/**
 * Validates a document ID against provider-specific rules and delegates status resolution to the
 * appropriate {@link ScannerGateway}.
 *
 * <p>Rules:
 *
 * <ul>
 *   <li>Document IDs must be exactly 12 characters.
 *   <li>Onfido documents must start with {@code ON-}.
 *   <li>Jumio documents must start with {@code JU-}.
 * </ul>
 */
@Service
public class DocumentValidator {

    static final String ONFIDO_PREFIX = "ON-";
    static final String JUMIO_PREFIX = "JU-";
    static final int DOCUMENT_ID_LENGTH = 12;

    /**
     * Validates the document ID format and delegates to the gateway for status resolution.
     *
     * @param gateway the provider gateway resolved by
     * @param token the scan token previously issued to the frontend
     * @param documentId the document ID sent back by the frontend
     * @return the validation status from the provider
     * @throws IllegalArgumentException if the document ID is invalid
     */
    public ScanValidationStatus validate(ScannerGateway gateway, String token, String documentId) {
        validateDocumentId(gateway, documentId);
        return gateway.validate(token, documentId);
    }

    private void validateDocumentId(ScannerGateway gateway, String documentId) {
        if (documentId == null || documentId.length() != DOCUMENT_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "Document ID must be exactly " + DOCUMENT_ID_LENGTH + " characters.");
        }

        String expectedPrefix = gateway.getDocumentPrefix();
        if (!documentId.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException(
                    "Document ID prefix does not match the assigned provider. "
                            + "Expected prefix: "
                            + expectedPrefix);
        }
    }
}
