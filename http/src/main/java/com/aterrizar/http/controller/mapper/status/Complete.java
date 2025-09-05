package com.aterrizar.http.controller.mapper.status;

import com.aterrizar.http.dto.StatusCode;
import org.springframework.http.HttpStatus;

public class Complete implements StatusMapperTemplate {
    @Override
    public StatusCode getStatus() {
        return StatusCode.COMPLETED;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.ACCEPTED;
    }
}
