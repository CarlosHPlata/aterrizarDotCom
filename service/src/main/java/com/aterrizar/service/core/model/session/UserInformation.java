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
        @Nullable String visaNumber)
        implements Serializable {
    public UserInformation withPassportNumber(String passportNumber) {
        return this.toBuilder().passportNumber(passportNumber).build();
    }

    public UserInformation withVisaNumber(String visaNumber) {
        return this.toBuilder().visaNumber(visaNumber).build();
    }
}
