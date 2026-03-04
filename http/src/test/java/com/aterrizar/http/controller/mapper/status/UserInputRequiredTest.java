package com.aterrizar.http.controller.mapper.status;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.FieldType;
import com.aterrizar.service.core.model.InputRequiredField;
import com.aterrizar.service.core.model.request.CheckinResponse;

class UserInputRequiredTest {

    @Test
    void shouldIncludeDynamicInputRequiredFieldInResponse() {
        var dynamicField =
                InputRequiredField.builder()
                        .id("token1_verified")
                        .name("Biometric verification")
                        .fieldType(FieldType.TEXT)
                        .build();
        var context =
                Context.builder()
                        .checkinResponse(
                                CheckinResponse.builder()
                                        .inputRequiredFields(Set.of(dynamicField))
                                        .build())
                        .build();

        var mapper = new UserInputRequired();
        var response = mapper.map(context);

        assertNotNull(response.getInputRequiredFields());
        assertTrue(
                response.getInputRequiredFields().stream()
                        .anyMatch(field -> "token1_verified".equals(field.getId())));
    }
}
