package com.aterrizar.service.core.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.FieldType;
import com.aterrizar.service.core.model.InputRequiredField;

class CheckinResponseTest {

    @Test
    void shouldNotDuplicateDynamicRequiredFieldWhenContentIsSame() {
        var response = CheckinResponse.builder().build();
        var first =
                InputRequiredField.builder()
                        .id("token1_verified")
                        .name("Biometric verification")
                        .fieldType(FieldType.TEXT)
                        .build();
        var second =
                InputRequiredField.builder()
                        .id("token1_verified")
                        .name("Biometric verification")
                        .fieldType(FieldType.TEXT)
                        .build();

        response.addInputRequiredField(first);
        response.addInputRequiredField(second);

        assertNotNull(response.inputRequiredFields());
        assertEquals(1, response.inputRequiredFields().size());
    }
}
