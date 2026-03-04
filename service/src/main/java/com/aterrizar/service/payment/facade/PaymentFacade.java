package com.aterrizar.service.payment.facade;

import com.aterrizar.service.payment.model.PaymentRequestDto;

public interface PaymentFacade {
    /**
     * Orquesta la obtención del adapter y el procesamiento del pago.
     *
     * @return el paymentToken generado por el mock externo.
     */
    String executePayment(PaymentRequestDto request);
}
