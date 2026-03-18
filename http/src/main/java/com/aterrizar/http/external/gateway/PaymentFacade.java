package com.aterrizar.http.external.gateway;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.payment.factory.PaymentAdapterFactory;
import com.aterrizar.service.core.model.PaymentRequestDto;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.PaymentGateway;

@Service
public class PaymentFacade implements PaymentGateway {

    private final PaymentAdapterFactory factory;

    public PaymentFacade(PaymentAdapterFactory factory) {
        this.factory = factory;
    }

    public String executePayment(PaymentRequestDto request) {
        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("paymentMethod cannot be null or empty");
        }

        var adapter = factory.getAdapter(request.paymentMethod());
        return adapter.processPayment(request);
    }

    @Override
    public String getPaymentStatus(String token) {
        return "SUCCESS";
    }

    @Override
    public Optional<RequiredField> validateRequiredFields(PaymentRequestDto dto) {
        if (dto.paymentMethod() == null) return Optional.of(RequiredField.PAYMENT_METHOD);

        return switch (dto.paymentMethod().toUpperCase()) {
            case "3DS" ->
                    (dto.cardNumber() == null || dto.cardNumber().isBlank())
                            ? Optional.of(RequiredField.CARD_NUMBER)
                            : Optional.empty();
            case "WIRE" ->
                    (dto.linkIdentifier() == null || dto.linkIdentifier().isBlank())
                            ? Optional.of(RequiredField.LINK_IDENTIFIER)
                            : Optional.empty();
            case "GOV" ->
                    (dto.curpNumber() == null || dto.curpNumber().isBlank())
                            ? Optional.of(RequiredField.CURP_NUMBER)
                            : Optional.empty();
            default -> Optional.empty();
        };
    }
}
