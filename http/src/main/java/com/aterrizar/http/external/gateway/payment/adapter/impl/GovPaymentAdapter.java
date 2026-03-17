package com.aterrizar.http.external.gateway.payment.adapter.impl;

import org.springframework.stereotype.Component;

import com.aterrizar.http.external.gateway.payment.adapter.PaymentAdapter;
import com.aterrizar.http.external.gateway.payment.port.ExternalPaymentPort;
import com.aterrizar.service.core.model.PaymentRequestDto;

@Component
public class GovPaymentAdapter implements PaymentAdapter {
    private static final String METHOD_NAME = "GOV";
    private final ExternalPaymentPort externalPaymentPort;

    public GovPaymentAdapter(ExternalPaymentPort externalPaymentPort) {
        this.externalPaymentPort = externalPaymentPort;
    }

    @Override
    public boolean supports(String paymentMethod) {
        return METHOD_NAME.equalsIgnoreCase(paymentMethod);
    }

    @Override
    public String processPayment(PaymentRequestDto request) {
        if (request.curpNumber() == null || request.curpNumber().isBlank()) {
            throw new IllegalArgumentException("curpNumber is required for GOV payment");
        }
        return externalPaymentPort.getGovToken(request);
    }
}
