package com.aterrizar.http.controller.mapper.status;

import com.aterrizar.http.dto.CheckinResponseData;
import com.aterrizar.http.dto.StatusCode;
import com.aterrizar.service.core.model.Context;
import org.springframework.http.HttpStatus;

public interface StatusMapperTemplate {
    default CheckinResponseData map(Context context) {
        var builder = CheckinResponseData.builder()
                .status(getStatus());
        return build(context, builder);
    }

    default CheckinResponseData build(Context context, CheckinResponseData.CheckinResponseDataBuilder builder) {
        return builder.build();
    }

    StatusCode getStatus();

    HttpStatus getHttpStatus();
}
