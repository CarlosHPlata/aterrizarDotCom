package com.aterrizar.service.external;

import java.util.Optional;

import com.aterrizar.service.core.model.PaymentRequestDto;
import com.aterrizar.service.core.model.RequiredField;

public interface PaymentGateway {
    String PAYMENT_SUCCESS_STATUS = "SUCCESS";

    String executePayment(PaymentRequestDto requestDto);

    String getPaymentStatus(String token);

    Optional<RequiredField> validateRequiredFields(PaymentRequestDto requestDto);
}
