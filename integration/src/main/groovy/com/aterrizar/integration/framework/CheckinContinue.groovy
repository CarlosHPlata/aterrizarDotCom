package com.aterrizar.integration.framework

import com.aterrizar.integration.model.UserInput
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

class CheckinContinue {
    private final RestClient client
    private String userId
    private String sessionId
    private String country
    private Map initResponse

    CheckinContinue(RestClient client, String country, String userId,  Map initResponse) {
        this.client = client
        this.userId = userId
        this.country = country
        this.initResponse = initResponse
        this.sessionId = initResponse.get("sessionId")
    }

    private Map continueCheckin(Map partialBody) {
        def body = [
                sessionId: this.sessionId,
                userId   : this.userId,
                country  : this.country
        ] + partialBody

        def response = client.post()
                .uri("/v1/checkin/continue")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class)

        return response
    }

    Map proceed() {
        return continueCheckin([:])
    }

    Map fillUserInput(Map<UserInput, String> userInput) {
        def mappedInput = userInput.collectEntries { key, value ->
            [(key.getId()): value]
        }
        return continueCheckin([providedFields: mappedInput])
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    void setCountry(String country) {
        this.country = country
    }

    Map getInitResponse() {
        return this.initResponse
    }
}
