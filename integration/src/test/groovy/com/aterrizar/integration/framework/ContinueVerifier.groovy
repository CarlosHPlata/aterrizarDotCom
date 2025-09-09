package com.aterrizar.integration.framework

import com.aterrizar.integration.model.Status
import com.aterrizar.integration.model.UserInput
import org.springframework.web.client.HttpClientErrorException.BadRequest

class ContinueVerifier {
    private final Map continueResponse
    private final BadRequest exception

    private ContinueVerifier(Map continueResponse) {
        this.continueResponse = continueResponse
        this.exception = null
    }

    private ContinueVerifier(Map continueResponse, BadRequest exception) {
        this.continueResponse = continueResponse
        this.exception = exception
    }

    static ContinueVerifier requiredField(Map continueResponse, UserInput expectedField) {
        return new ContinueVerifier(continueResponse)
                .hasStatus(Status.USER_INPUT_REQUIRED.value)
                .hasRequiredFieldInBody(expectedField.getId())
    }

    static ContinueVerifier rejected(BadRequest exception, String expectedMessage) {
        return new ContinueVerifier([:], exception)
                .hasBadRequestError()
                .hasErrorMessage(expectedMessage)
                .hasRejectedInBody()
    }

    static ContinueVerifier completed(Map continueResponse) {
        return new ContinueVerifier(continueResponse)
                .hasStatus(Status.COMPLETED.value)
    }

    private ContinueVerifier hasRequiredFieldInBody(String expectedField) {
        assert continueResponse.inputRequiredFields.any { it['id'] == expectedField }
        return this
    }

    private ContinueVerifier hasStatus(String expectedStatus) {
        assert continueResponse.status == expectedStatus
        return this
    }

    private ContinueVerifier hasBadRequestError() {
        assert exception instanceof BadRequest
        return this
    }

    private ContinueVerifier hasErrorMessage(String expectedMessage) {
        assert exception.message.contains(expectedMessage)
        return this
    }

    private ContinueVerifier hasRejectedInBody() {
        assert exception.responseBodyAsString.contains("rejected")
        return this
    }
}
