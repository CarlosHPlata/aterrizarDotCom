package com.aterrizar.http.external.gateway.passport;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.aterrizar.http.config.BaseUrl;

@BaseUrl("${http.client.passport.base.url}")
@HttpExchange(value = "v1/passport", accept = "application/json", contentType = "application/json")
public interface PassportHttpClient {
    @PostExchange("passport")
    boolean validatePassport(@RequestParam("p") String passportNumber);
}
