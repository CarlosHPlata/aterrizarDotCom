package com.aterrizar.service.core.model.session;

import java.io.Serializable;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserInformation(
        UUID userId,
        @Nullable String email,
        @Nullable String passportNumber,
        @Nullable String fullName,
        @Nullable String visaNumber,
        @Nullable Double usFunds,
        @Nullable String scanToken,
        @Nullable String documentId,
        @Nullable Integer idScanRetries)
        implements Serializable {
    public UserInformation withPassportNumber(String passportNumber) {
        return this.toBuilder().passportNumber(passportNumber).build();
    }

    public UserInformation withVisaNumber(String visaNumber) {
        return this.toBuilder().visaNumber(visaNumber).build();
    }

    public UserInformation withScanToken(String scanToken) {
        return this.toBuilder().scanToken(scanToken).build();
    }

    public UserInformation withDocumentId(String documentId) {
        return this.toBuilder().documentId(documentId).build();
    }

    public int resolvedRetries() {
        return idScanRetries != null ? idScanRetries : 0;
    }
}
