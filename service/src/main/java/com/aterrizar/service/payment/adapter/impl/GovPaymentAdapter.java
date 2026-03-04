package com.aterrizar.service.payment.adapter.impl;

import org.springframework.stereotype.Component;

import com.aterrizar.service.payment.adapter.PaymentAdapter;
import com.aterrizar.service.payment.model.PaymentRequestDto;
import com.aterrizar.service.payment.port.ExternalPaymentPort;

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
