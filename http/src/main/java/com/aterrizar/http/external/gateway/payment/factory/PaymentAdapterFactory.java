package com.aterrizar.http.external.gateway.payment.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.aterrizar.http.external.gateway.payment.adapter.PaymentAdapter;

@Component
public class PaymentAdapterFactory {

    private final List<PaymentAdapter> adapters;

    public PaymentAdapterFactory(List<PaymentAdapter> adapters) {
        this.adapters = adapters;
    }

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
