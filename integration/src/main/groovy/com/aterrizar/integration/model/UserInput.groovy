package com.aterrizar.integration.model

enum UserInput {
    PASSPORT_NUMBER("PASSPORT_NUMBER", "Passport Number"),
    FULL_NAME("FULL_NAME", "Full Name"),
    EMAIL("EMAIL", "Email"),
    AGREEMENT_SIGNED("AGREEMENT_SIGNED", "Agreement Signed"),
    VISA_NUMBER("VISA_NUMBER", "Digital Visa Number"),
    FUNDS_AMOUNT_US("FUNDS_AMOUNT_US", "Funds Amount (US)"),
    PAYMENT_METHOD("PAYMENT_METHOD", "Payment Method"),
    CARD_NUMBER("CARD_NUMBER", "Card Number"),
    LINK_IDENTIFIER("LINK_IDENTIFIER", "Link Identifier"),
    CURP_NUMBER("CURP_NUMBER", "Curp Number")

    private final String value
    private final String id

    UserInput(String id, String value) {
        this.id = id
        this.value = value
    }

    String getValue() {
        return value
    }

    String getId() {
        return id
    }
}