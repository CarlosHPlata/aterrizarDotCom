package com.aterrizar.service.payment.facade.impl;

import org.springframework.stereotype.Service;

import com.aterrizar.service.payment.facade.PaymentFacade;
import com.aterrizar.service.payment.factory.PaymentAdapterFactory;
import com.aterrizar.service.payment.model.PaymentRequestDto;

@Service
public class PaymentFacadeImpl implements PaymentFacade {

    private final PaymentAdapterFactory factory;

    public PaymentFacadeImpl(PaymentAdapterFactory factory) {
        this.factory = factory;
    }

    @Override
    public String executePayment(PaymentRequestDto request) {
        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("paymentMethod cannot be null or empty");
        }

        var adapter = factory.getAdapter(request.paymentMethod());
        return adapter.processPayment(request);
    }
}
