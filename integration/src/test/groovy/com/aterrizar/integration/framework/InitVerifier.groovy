package com.aterrizar.integration.framework

import com.aterrizar.integration.model.Status

class InitVerifier {
    private static final String INITIALIZED_STATUS = Status.INITIALIZED.value
    private final Map session

    private InitVerifier(Map session) {
        this.session = session
    }

    static InitVerifier verify(CheckinContinue session) {
        return new InitVerifier(session.getInitResponse())
                .hasStatus(INITIALIZED_STATUS)
                .hasValidSessionId()
    }

    private InitVerifier hasValidSessionId() {
        assert session.sessionId != null
        return this
    }

    private InitVerifier hasStatus(String expectedStatus) {
        assert session.status == expectedStatus
        return this
    }
}
