package com.aterrizar.service.core.model.session;

import java.io.Serializable;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder(toBuilder = true)
public record Session(
        UUID sessionId,
        SessionData sessionData,
        Status status,
        UserInformation userInformation,
        ExperimentalData experimentalData,
        boolean biometricAuthenticated,
        boolean biometricFailed,
        @Nullable String biometricSessionToken)
        implements Serializable {

    public boolean isBiometricAuthenticated() {
        return biometricAuthenticated;
    }

    public boolean isBiometricFailed() {
        return biometricFailed;
    }

    public Session withStatus(Status status) {
        return this.toBuilder().status(status).build();
    }

    public Session withSessionData(SessionData sessionData) {
        return this.toBuilder().sessionData(sessionData).build();
    }

    public Session withUserInformation(UserInformation userInformation) {
        return this.toBuilder().userInformation(userInformation).build();
    }

    public Session withExperimentalData(ExperimentalData experimentalData) {
        return this.toBuilder().experimentalData(experimentalData).build();
    }
}
