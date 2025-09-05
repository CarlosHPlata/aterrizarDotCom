package com.aterrizar.service.core.model.session;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record UserInformation (
        UUID userId,
        @Nullable
        String email,
        @Nullable
        String passportNumber,
        @Nullable
        String fullName
){
        public UserInformation withPassportNumber(String passportNumber) {
                return this.toBuilder().passportNumber(passportNumber).build();
        }
}
