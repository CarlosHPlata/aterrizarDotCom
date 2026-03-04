package com.aterrizar.service.external;

import com.aterrizar.service.core.model.biometric.BiometricStart;

public interface BiometricGateway {
    BiometricStart start();

    boolean verify(String signedToken);
}
