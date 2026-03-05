package com.aterrizar.http.external.gateway.scanner;

import com.aterrizar.http.external.gateway.scanner.model.ValidateRequest;
import com.aterrizar.service.core.model.ValidationStatus;
import com.aterrizar.service.core.model.session.Status;
import com.neovisionaries.i18n.CountryCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DocumentValidator {
    @Value("${feature.onfido.enabled.countries}")
    private List<String> onfidoEnabledCountries;

    private final ScannerHttpClient scannerHttpClient;

    private static final String ONFIDO_PREFIX = "ON-";
    private static final String JUMIO_PREFIX = "JU-";
    private static final int REQUIRED_LENGTH = 12;

    public DocumentValidator(ScannerHttpClient scannerHttpClient) {
        this.scannerHttpClient = scannerHttpClient;
    }

    public ValidationStatus validate(String token, String documentId, CountryCode countryCode) {

        log.info("Validating document - token: {}, documentId: {}, country: {}", token, documentId, countryCode);
        ValidationStatus validationStatus;

        if (documentId.length() != REQUIRED_LENGTH) {
            validationStatus = ValidationStatus.REJECTED;
        }

        String expectedPrefix = onfidoEnabledCountries.contains(countryCode.toString().toUpperCase()) ? ONFIDO_PREFIX : JUMIO_PREFIX;

        ValidateRequest validateRequest = new ValidateRequest(token, documentId);
        if (documentId.startsWith(expectedPrefix)) {
            validationStatus = scannerHttpClient.validateDocument(validateRequest).status();
            log.info("Scanner response: {}", validationStatus);
        } else {
            validationStatus = ValidationStatus.REJECTED;
        }

        return validationStatus;
    }
}
