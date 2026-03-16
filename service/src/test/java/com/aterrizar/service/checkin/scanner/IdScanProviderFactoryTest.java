package com.aterrizar.service.checkin.scanner;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.feature.OnfidoFeature;
import com.aterrizar.service.external.scanner.ScannerGateway;

@ExtendWith(MockitoExtension.class)
class IdScanProviderFactoryTest {

    @Mock private OnfidoFeature onfidoFeature;
    @Mock private ScannerGateway onfidoGateway;
    @Mock private ScannerGateway jumioGateway;

    private IdScanProviderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new IdScanProviderFactory(onfidoFeature, onfidoGateway, jumioGateway);
    }

    @Test
    void shouldReturnOnfidoGatewayForHighSecurityCountry() {
        when(onfidoFeature.isCountryAvailable("US")).thenReturn(true);

        var provider = factory.getProvider("US");

        assertSame(onfidoGateway, provider);
    }

    @Test
    void shouldReturnJumioGatewayForStandardCountry() {
        when(onfidoFeature.isCountryAvailable("MX")).thenReturn(false);

        var provider = factory.getProvider("MX");

        assertSame(jumioGateway, provider);
    }

    @Test
    void shouldReturnJumioGatewayWhenCountryIsNotInOnfidoList() {
        when(onfidoFeature.isCountryAvailable("AR")).thenReturn(false);

        var provider = factory.getProvider("AR");

        assertSame(jumioGateway, provider);
    }
}
