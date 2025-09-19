package com.aterrizar.integration.model

enum UserInput {
    PASSPORT_NUMBER("PASSPORT_NUMBER", "Passport Number"),
    FULL_NAME("FULL_NAME", "Full Name"),
    EMAIL("EMAIL", "Email"),
    AGREEMENT_SIGNED("AGREEMENT_SIGNED", "Agreement Signed"),
    HEALTH_CLEAR_ACKNOWLEDGEMENT("HEALTH_CLEAR_ACKNOWLEDGEMENT", "Health Clear Acknowledgement")

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