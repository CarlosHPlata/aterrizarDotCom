package com.aterrizar.service.core.model;

import lombok.Getter;

@Getter
public enum RequiredField {
    PASSPORT_NUMBER("PASSPORT_NUMBER", "Passport Number", FieldType.TEXT),
    FULL_NAME("FULL_NAME", "Full Name", FieldType.TEXT),
    EMAIL("EMAIL", "Email", FieldType.EMAIL),
    AGREEMENT_SIGNED("AGREEMENT_SIGNED", "Agreement Signed", FieldType.BOOLEAN),
    FUNDS_AMOUNT_US("FUNDS_AMOUNT_US", "US Funds", FieldType.NUMBER);

    private final String value;
    private final String id;
    private final FieldType fieldType;

    RequiredField(String id, String value, FieldType fieldType) {
        this.id = id;
        this.value = value;
        this.fieldType = fieldType;
    }
}
