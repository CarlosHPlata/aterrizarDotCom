package com.aterrizar.http.controller.mapper;

import org.springframework.http.ResponseEntity;

import com.aterrizar.http.dto.CountryCode;
import com.aterrizar.service.core.model.Context;

public interface RequestMapperStrategy<T extends Context, R, P> {
    Context mapRequestToContext(P request);

    ResponseEntity<R> mapContextToResponse(T context);

    default com.neovisionaries.i18n.CountryCode mapCountry(CountryCode countryCode) {
        return com.neovisionaries.i18n.CountryCode.valueOf(countryCode.name());
    }
}
