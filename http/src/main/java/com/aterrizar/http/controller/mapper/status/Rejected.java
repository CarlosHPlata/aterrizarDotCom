package com.aterrizar.http.controller.mapper.status;

import com.aterrizar.http.dto.CheckinResponseData;
import com.aterrizar.http.dto.StatusCode;
import com.aterrizar.service.core.model.Context;
import org.springframework.http.HttpStatus;

public class Rejected implements StatusMapperTemplate {
    @Override
    public StatusCode getStatus() {
        return StatusCode.REJECTED;
    }

    @Override
    public CheckinResponseData build(Context context, CheckinResponseData.CheckinResponseDataBuilder builder) {
        return builder
                .errorMessage(context.checkinResponse().errorMessage())
                .build();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
