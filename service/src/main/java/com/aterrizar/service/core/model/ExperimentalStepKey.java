package com.aterrizar.service.core.model;

public enum ExperimentalStepKey {
    AGREEMENT_SIGN("agreementdrop");

    private final String value;

    private ExperimentalStepKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
