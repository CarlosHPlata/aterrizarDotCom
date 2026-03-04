package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.payment.adapter.impl.GovPaymentAdapter;
import com.aterrizar.service.payment.adapter.impl.ThreeDsPaymentAdapter;
import com.aterrizar.service.payment.adapter.impl.WireTransferPaymentAdapter;
import com.aterrizar.service.payment.model.PaymentRequestDto;
import com.aterrizar.service.payment.port.ExternalPaymentPort;

class PaymentAdapterTest {

    private ExternalPaymentPort port;
    private GovPaymentAdapter govAdapter;
    private ThreeDsPaymentAdapter threeDsAdapter;
    private WireTransferPaymentAdapter wireAdapter;

    @BeforeEach
    void setUp() {
        port = mock(ExternalPaymentPort.class);
        govAdapter = new GovPaymentAdapter(port);
        threeDsAdapter = new ThreeDsPaymentAdapter(port);
        wireAdapter = new WireTransferPaymentAdapter(port);
    }

    @Test
    void govAdapterShouldThrowExceptionWhenCurpIsMissing() {
        var request = new PaymentRequestDto("GOV", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> govAdapter.processPayment(request));
    }

    @Test
    void threeDsAdapterShouldCallPortWhenCardIsPresent() {
        var request = new PaymentRequestDto("3DS", "1234-5678", null, null);
        when(port.get3dsToken(request)).thenReturn("3DS-TOKEN");

        String result = threeDsAdapter.processPayment(request);

        assertEquals("3DS-TOKEN", result);
        verify(port, times(1)).get3dsToken(request);
    }

    @Test
    void wireAdapterShouldCallPortWhenLinkIdentifierIsPresent() {
        var request = new PaymentRequestDto("WIRE", null, "LINK-999", null);
        when(port.getWireToken(request)).thenReturn("WIRE-TOKEN");

        String result = wireAdapter.processPayment(request);

        assertEquals("WIRE-TOKEN", result);
        verify(port, times(1)).getWireToken(request);
    }

    @Test
    void wireAdapterShouldThrowExceptionWhenLinkIdentifierIsMissing() {
        var request = new PaymentRequestDto("WIRE", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> wireAdapter.processPayment(request));
    }
}
