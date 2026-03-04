package com.aterrizar.service.external;

public interface BiometricGateway {
    String startSession();

    boolean verifySession(String authSessionToken);
}
