package com.aterrizar.http.external.gateway.payment.port;

import com.aterrizar.service.core.model.PaymentRequestDto;

public interface ExternalPaymentPort {

    String getWireToken(PaymentRequestDto request);

    String getGovToken(PaymentRequestDto request);

    String getPaymentStatus(String paymentToken);

    String get3dsToken(PaymentRequestDto request);
}
