package com.aterrizar.service.external;

import com.aterrizar.service.core.model.ValidationStatus;
import com.neovisionaries.i18n.CountryCode;

public interface ScannerGateway {
    String generateToken(String provider);

    ValidationStatus validateDocument(String token, String documentId, CountryCode countryCode);
}
