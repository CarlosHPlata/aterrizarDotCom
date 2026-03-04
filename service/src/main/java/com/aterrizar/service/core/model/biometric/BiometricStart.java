package com.aterrizar.service.core.model.biometric;

import lombok.Builder;

@Builder
public record BiometricStart(String authSessionToken) {}
