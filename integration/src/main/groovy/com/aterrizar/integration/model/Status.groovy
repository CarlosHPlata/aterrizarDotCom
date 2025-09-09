package com.aterrizar.integration.model

enum Status {
    INITIALIZED('initialized'),
    REJECTED('rejected'),
    USER_INPUT_REQUIRED("user_input_required"),
    COMPLETED('completed')

    final String value

    private Status(String value) {
        this.value = value
    }
}