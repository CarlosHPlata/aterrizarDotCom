package com.aterrizar.service.core.model.session;

import lombok.Getter;

@Getter
public enum Status {
    COMPLETED("completed"),
    INITIALIZED("initialized"),
    REJECTED("rejected"),
    USER_INPUT_REQUIRED("user_input_required"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
