package com.aterrizar.http.external.gateway.homeoffice;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.gateway.homeoffice.model.EtaRequestDto;
import com.aterrizar.service.external.HomeOfficeGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HomeOfficeAdapter implements HomeOfficeGateway {

    private final HomeOfficeApiClient homeOfficeApiClient;

    @Override
    public String validateEta(String passportNumber, String destinationCode) {
        var requestDto = new EtaRequestDto(passportNumber, destinationCode);
        var responseDto = homeOfficeApiClient.validateEta(requestDto);
        return responseDto.status();
    }
}
