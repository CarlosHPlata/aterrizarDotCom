package com.aterrizar.service.payment.adapter;

import com.aterrizar.service.payment.model.PaymentRequestDto;

public interface PaymentAdapter {
    /** Define si este adapter soporta el método de pago solicitado. */
    boolean supports(String paymentMethod);

    /**
     * Procesa la solicitud y retorna el paymentToken. Aquí se lanzará una excepción si el DTO no
     * contiene el campo requerido.
     */
    String processPayment(PaymentRequestDto request);
}
