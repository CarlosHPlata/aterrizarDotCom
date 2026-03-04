package com.aterrizar.service.core.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class InputRequiredField {
    private final String id;
    private final String name;
    private final FieldType fieldType;
}
