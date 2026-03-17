package com.aterrizar.http.external.gateway.payment.adapter.impl;

import org.springframework.stereotype.Component;

import com.aterrizar.http.external.gateway.payment.adapter.PaymentAdapter;
import com.aterrizar.http.external.gateway.payment.port.ExternalPaymentPort;
import com.aterrizar.service.core.model.PaymentRequestDto;

@Component
public class ThreeDsPaymentAdapter implements PaymentAdapter {
    private static final String METHOD_NAME = "3DS";
    private final ExternalPaymentPort externalPaymentPort;

    public ThreeDsPaymentAdapter(ExternalPaymentPort externalPaymentPort) {
        this.externalPaymentPort = externalPaymentPort;
    }

    @Override
    public boolean supports(String paymentMethod) {
        return METHOD_NAME.equalsIgnoreCase(paymentMethod);
    }

    @Override
    public String processPayment(PaymentRequestDto request) {
        if (request.cardNumber() == null || request.cardNumber().isBlank()) {
            throw new IllegalArgumentException("cardNumber is required for 3DS payment");
        }
        return externalPaymentPort.get3dsToken(request);
    }
}
