package com.aterrizar.http.external.gateway.payment.adapter;

import com.aterrizar.service.core.model.PaymentRequestDto;

public interface PaymentAdapter {
    boolean supports(String paymentMethod);

    /** Here we will throw an exception if the DTO does not contain the required field. */
    String processPayment(PaymentRequestDto request);
}
