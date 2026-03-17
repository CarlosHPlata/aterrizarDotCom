package com.aterrizar.service.core.model;

import java.util.Map;

public record PaymentRequestDto(
        String paymentMethod, String cardNumber, String linkIdentifier, String curpNumber) {
    public static PaymentRequestDto fromProvidedFields(Map<RequiredField, String> providedFields) {
        if (providedFields == null) {
            return new PaymentRequestDto(null, null, null, null);
        }
        return new PaymentRequestDto(
                providedFields.get(RequiredField.PAYMENT_METHOD),
                providedFields.get(RequiredField.CARD_NUMBER),
                providedFields.get(RequiredField.LINK_IDENTIFIER),
                providedFields.get(RequiredField.CURP_NUMBER));
    }
}
