package com.aterrizar.http.controller.mapper.status;

import com.aterrizar.http.dto.CheckinResponseData;
import com.aterrizar.http.dto.CheckinResponseDataRequiredFieldsInner;
import com.aterrizar.http.dto.StatusCode;
import com.aterrizar.service.core.model.Context;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class UserInputRequired implements StatusMapperTemplate {
    @Override
    public CheckinResponseData build(Context context, CheckinResponseData.CheckinResponseDataBuilder builder) {
        return builder
                .inputRequiredFields(inputFields(context))
                .build();
    }

    @Override
    public StatusCode getStatus() {
        return StatusCode.USER_INPUT_REQUIRED;
    }

    private List<CheckinResponseDataRequiredFieldsInner> inputFields(Context context) {
        return context.checkinResponse()
                .providedFields()
                .stream()
                .map(field -> CheckinResponseDataRequiredFieldsInner.builder()
                        .id(field.getId())
                        .name(field.getValue())
                        .type(field.getFieldType().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.PARTIAL_CONTENT;
    }
}
