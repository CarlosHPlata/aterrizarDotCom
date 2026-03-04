package com.aterrizar.http.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.aterrizar.http.dto.CheckinRequestData;
import com.aterrizar.http.dto.CountryCode;
import com.aterrizar.service.core.model.RequiredField;

class CheckinMapperTest {

    @Test
    void shouldIgnoreUnknownProvidedFieldsAndPreserveRawMap() {
        var mapper = new CheckinMapper();
        var requestData =
                CheckinRequestData.builder()
                        .sessionId(UUID.randomUUID())
                        .userId(UUID.randomUUID())
                        .country(CountryCode.US)
                        .providedFields(
                                Map.of(
                                        "additionalProp1", "x",
                                        "PASSPORT_NUMBER", "A12345678"))
                        .build();

        var context = assertDoesNotThrow(() -> mapper.mapRequestToContext(requestData));
        var request = context.checkinRequest();

        assertEquals("x", request.rawProvidedFields().get("additionalProp1"));
        assertTrue(request.providedFields().containsKey(RequiredField.PASSPORT_NUMBER));
        assertEquals("A12345678", request.providedFields().get(RequiredField.PASSPORT_NUMBER));
        assertEquals(1, request.providedFields().size());
    }

    @Test
    void shouldThrowWhenKnownNumberFieldHasInvalidValue() {
        var mapper = new CheckinMapper();
        var requestData =
                CheckinRequestData.builder()
                        .sessionId(UUID.randomUUID())
                        .userId(UUID.randomUUID())
                        .country(CountryCode.US)
                        .providedFields(Map.of("FUNDS_AMOUNT_US", "abc"))
                        .build();

        assertThrows(IllegalArgumentException.class, () -> mapper.mapRequestToContext(requestData));
    }
}
