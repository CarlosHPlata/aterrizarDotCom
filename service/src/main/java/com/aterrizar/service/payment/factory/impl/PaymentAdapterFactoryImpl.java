package com.aterrizar.service.payment.factory.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.aterrizar.service.payment.adapter.PaymentAdapter;
import com.aterrizar.service.payment.factory.PaymentAdapterFactory;

@Component
public class PaymentAdapterFactoryImpl implements PaymentAdapterFactory {

    private final List<PaymentAdapter> adapters;

    public PaymentAdapterFactoryImpl(List<PaymentAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public PaymentAdapter getAdapter(String paymentMethod) {
        return adapters.stream()
                .filter(adapter -> adapter.supports(paymentMethod))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Unsupported payment method: " + paymentMethod));
    }
}
