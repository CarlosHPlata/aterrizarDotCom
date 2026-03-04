package com.aterrizar.service.payment.adapter;

import com.aterrizar.service.payment.model.PaymentRequestDto;

public interface PaymentAdapter {
    /** Define if the adapter supports the given payment method */
    boolean supports(String paymentMethod);

    /**
     * 
     * Here we will throw an exception if the DTO does not contain the required field
     * 
     */
    String processPayment(PaymentRequestDto request);
}
