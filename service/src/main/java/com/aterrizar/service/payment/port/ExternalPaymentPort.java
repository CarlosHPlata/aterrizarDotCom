package com.aterrizar.service.payment.port;

import com.aterrizar.service.payment.model.PaymentRequestDto;

public interface ExternalPaymentPort {
    String get3dsToken(PaymentRequestDto request);

    String getWireToken(PaymentRequestDto request);

    String getGovToken(PaymentRequestDto request);

    String getPaymentStatus(String paymentToken);
}
