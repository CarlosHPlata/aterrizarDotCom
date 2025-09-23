package com.aterrizar.http.external.gateway.passport;

import org.springframework.stereotype.Service;

import com.aterrizar.service.external.PassportGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PassportGatewayAdapter implements PassportGateway {
    private final PassportHttpClient client;

    @Override
    public boolean validate(String passportNumber) {
        return client.validatePassport(passportNumber);
    }
}
