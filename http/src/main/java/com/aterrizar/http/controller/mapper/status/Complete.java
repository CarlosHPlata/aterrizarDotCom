package com.aterrizar.http.controller.mapper.status;

import org.springframework.http.HttpStatus;

import com.aterrizar.http.dto.StatusCode;

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
