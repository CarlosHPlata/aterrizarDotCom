package com.aterrizar.service.checkin.scanner;

import org.springframework.stereotype.Component;

@Component
public class DocumentValidator {
    private static final int REQUIRED_LENGTH = 12;

    public boolean isValid(String documentId, String expectedPrefix) {
        if (documentId == null || documentId.isBlank()) {
            return false;
        }

        if (documentId.length() != REQUIRED_LENGTH) {
            return false;
        }

        return documentId.startsWith(expectedPrefix);
    }
}
