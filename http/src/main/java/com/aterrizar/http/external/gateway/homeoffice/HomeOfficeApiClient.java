package com.aterrizar.http.external.gateway.homeoffice;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.homeoffice.model.EtaRequestDto;
import com.aterrizar.http.external.gateway.homeoffice.model.EtaResponseDto;

@BaseUrl("${http.client.homeoffice.base.url}")
@HttpExchange(accept = "application/json", contentType = "application/json")
public interface HomeOfficeApiClient {

    @PostExchange("/eta-validation")
    EtaResponseDto validateEta(@RequestBody EtaRequestDto request);
}
