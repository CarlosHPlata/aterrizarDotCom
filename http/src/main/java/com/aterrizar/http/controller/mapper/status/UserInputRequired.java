package com.aterrizar.http.controller.mapper.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;

import com.aterrizar.http.dto.CheckinResponseData;
import com.aterrizar.http.dto.CheckinResponseDataRequiredFieldsInner;
import com.aterrizar.http.dto.StatusCode;
import com.aterrizar.service.core.model.Context;

public class UserInputRequired implements StatusMapperTemplate {
    @Override
    public CheckinResponseData build(
            Context context, CheckinResponseData.CheckinResponseDataBuilder builder) {
        return builder.inputRequiredFields(inputFields(context)).build();
    }

    @Override
    public StatusCode getStatus() {
        return StatusCode.USER_INPUT_REQUIRED;
    }

    private List<CheckinResponseDataRequiredFieldsInner> inputFields(Context context) {
        var response = context.checkinResponse();
        if (response == null) {
            return List.of();
        }

        Stream<CheckinResponseDataRequiredFieldsInner> enumFields;
        if (response.providedFields() == null) {
            enumFields = Stream.empty();
        } else {
            enumFields =
                    response.providedFields().stream()
                            .map(
                                    field ->
                                            CheckinResponseDataRequiredFieldsInner.builder()
                                                    .id(field.getId())
                                                    .name(field.getValue())
                                                    .type(field.getFieldType().name())
                                                    .build());
        }

        Stream<CheckinResponseDataRequiredFieldsInner> dynamicFields;
        if (response.inputRequiredFields() == null) {
            dynamicFields = Stream.empty();
        } else {
            dynamicFields =
                    response.inputRequiredFields().stream()
                            .map(
                                    field ->
                                            CheckinResponseDataRequiredFieldsInner.builder()
                                                    .id(field.id())
                                                    .name(field.name())
                                                    .type(field.fieldType().name())
                                                    .build());
        }

        return Stream.concat(enumFields, dynamicFields).collect(Collectors.toList());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PARTIAL_CONTENT;
    }
}
