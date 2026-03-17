package com.aterrizar.http.external.gateway.payment.adapter.impl;

import org.springframework.stereotype.Component;

import com.aterrizar.http.external.gateway.payment.adapter.PaymentAdapter;
import com.aterrizar.http.external.gateway.payment.port.ExternalPaymentPort;
import com.aterrizar.service.core.model.PaymentRequestDto;

@Component
public class WireTransferPaymentAdapter implements PaymentAdapter {
    private static final String METHOD_NAME = "WIRE";
    private final ExternalPaymentPort externalPaymentPort;

    public WireTransferPaymentAdapter(ExternalPaymentPort externalPaymentPort) {
        this.externalPaymentPort = externalPaymentPort;
    }

    @Override
    public boolean supports(String paymentMethod) {
        return METHOD_NAME.equalsIgnoreCase(paymentMethod);
    }

    @Override
    public String processPayment(PaymentRequestDto request) {
        if (request.linkIdentifier() == null || request.linkIdentifier().isBlank()) {
            throw new IllegalArgumentException("linkIdentifier is required for WIRE payment");
        }
        return externalPaymentPort.getWireToken(request);
    }
}
