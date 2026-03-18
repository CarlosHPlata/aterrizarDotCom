package com.aterrizar.http.external.gateway.tax;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.tax.model.v1.TaxDto;

@BaseUrl("${http.client.tax.base.url}")
@HttpExchange(value = "v1/", accept = "application/json", contentType = "application/json")
public interface TaxHttpClient {
    @GetExchange("calculate")
    TaxDto getTax(@RequestParam("rfc") String rfc);
}
