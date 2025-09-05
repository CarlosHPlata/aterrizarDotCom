package com.aterrizar.http.controller.mapper.fields;

import com.aterrizar.service.core.model.FieldType;
import com.aterrizar.service.core.model.RequiredField;
import org.springframework.cglib.core.internal.Function;
import org.yaml.snakeyaml.util.Tuple;

import java.util.Map;

public class RequiredFieldMapper {
    public static Tuple<RequiredField, String> map(String fieldName, String fieldValue) {
        var field = RequiredField.valueOf(fieldName);
        var mapper = getFieldMappersStrategies().get(field.getFieldType());

        if (mapper == null) {
            throw new IllegalStateException("No mapper found for field type: " + field.getFieldType());
        }

        return new Tuple<>(field, mapper.apply(fieldValue));
    }

    private static Map<FieldType, Function<String, String>> getFieldMappersStrategies() {
        return Map.of(
                FieldType.TEXT, RequiredFieldMapper::mapTextField,
                FieldType.EMAIL, RequiredFieldMapper::mapEmailField,
                FieldType.BOOLEAN, RequiredFieldMapper::mapBoolean
        );
    }

    private static String mapTextField(String value) {
        return value;
    }

    private static String mapEmailField(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return value;
    }

    private static String mapBoolean(String value) {
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Invalid boolean format");
        }
        return value.toLowerCase();
    }
}
