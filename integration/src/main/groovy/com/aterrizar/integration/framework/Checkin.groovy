package com.aterrizar.integration.framework

import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

class Checkin {
    private final static String BASE_URL = "http://localhost:8080/aterrizar"
    private final RestClient client
    private String userId

    private Checkin(RestClient client) {
        this.client = client
    }

    private Checkin(RestClient client, String userId) {
        this.client = client
        this.userId = userId
    }

    private static RestClient getClient() {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .build()
    }

    static Checkin create() {
        return new Checkin(getClient())
    }

    static Checkin create(String userId) {
        return new Checkin(getClient(), userId)
    }

    CheckinContinue initSession(String country, Map<String, Object> partialBody) {
        def userIdToUse = this.userId ?: "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        def defaultBody = [
                country      : country,
                userId       : userIdToUse,
                passengers   : 2,
                flightNumbers: ["AR1234", "AR5678"]
        ]
        def mergedBody = defaultBody + partialBody

        def response = client.post()
                .uri("/v1/checkin/init")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mergedBody)
                .retrieve()
                .body(Map.class)

        new CheckinContinue(this.client, country, userIdToUse, response)
    }

    CheckinContinue initSession(String country) {
        initSession(country, [:])
    }
}