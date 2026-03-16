package com.aterrizar.service.checkin.scanner;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.feature.OnfidoFeature;
import com.aterrizar.service.external.scanner.ScannerGateway;

/**
 * Factory that resolves the correct {@link ScannerGateway} implementation based on the passenger's
 * country code.
 *
 * <p>Routing rules:
 *
 * <ul>
 *   <li>Countries listed in {@code feature.onfido.enabled.countries} → Onfido (high-security)
 *   <li>All other countries → Jumio (standard)
 * </ul>
 */
@Service
public class IdScanProviderFactory {

    private final OnfidoFeature onfidoFeature;
    private final ScannerGateway onfidoGateway;
    private final ScannerGateway jumioGateway;

    public IdScanProviderFactory(
            OnfidoFeature onfidoFeature,
            @Qualifier("onfidoGatewayAdapter") ScannerGateway onfidoGateway,
            @Qualifier("jumioGatewayAdapter") ScannerGateway jumioGateway) {
        this.onfidoFeature = onfidoFeature;
        this.onfidoGateway = onfidoGateway;
        this.jumioGateway = jumioGateway;
    }

    /**
     * Returns the appropriate {@link ScannerGateway} for the given country.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @return the selected provider gateway
     */
    public ScannerGateway getProvider(String countryCode) {
        if (onfidoFeature.isCountryAvailable(countryCode)) {
            return onfidoGateway;
        }
        return jumioGateway;
    }
}
