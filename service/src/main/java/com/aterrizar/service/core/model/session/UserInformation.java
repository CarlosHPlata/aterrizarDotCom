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
        @Nullable String rfc,
        @Nullable Double taxAmount,
        @Nullable Boolean digitalSign)
        implements Serializable {
    public UserInformation withPassportNumber(String passportNumber) {
        return this.toBuilder().passportNumber(passportNumber).build();
    }

    public UserInformation withVisaNumber(String visaNumber) {
        return this.toBuilder().visaNumber(visaNumber).build();
    }

    public UserInformation withRfc(String rfc) {
        return this.toBuilder().rfc(rfc).build();
    }

    public UserInformation withTaxAmount(Double taxAmount) {
        return this.toBuilder().taxAmount(taxAmount).build();
    }

    public UserInformation withDigitalSign(Boolean digitalSign) {
        return this.toBuilder().digitalSign(digitalSign).build();
    }
}
