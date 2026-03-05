package com.aterrizar.http.external.gateway.scanner.model;

import com.neovisionaries.i18n.CountryCode;

public record ValidateRequest(String token, String documentId) {}
