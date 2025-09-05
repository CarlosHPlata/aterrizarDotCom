package com.aterrizar.http.controller.mapper;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aterrizar.http.dto.InitRequestData;
import com.aterrizar.http.dto.InitResponseData;
import com.aterrizar.http.dto.StatusCode;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.init.SessionRequest;

public class InitMapper
    implements RequestMapperStrategy<InitContext, InitResponseData, InitRequestData> {
  @Override
  public InitContext mapRequestToContext(InitRequestData initRequestData) {
    var sessionRequest =
        SessionRequest.builder()
            .countryCode(
                com.neovisionaries.i18n.CountryCode.valueOf(initRequestData.getCountry().name()))
            .passengers(initRequestData.getPassengers())
            .userId(initRequestData.getUserId())
            .flights(initRequestData.getFlightNumbers())
            .build();

    return InitContext.builder().sessionRequest(Optional.of(sessionRequest)).build();
  }

  @Override
  public ResponseEntity<InitResponseData> mapContextToResponse(InitContext context) {
    var response =
        InitResponseData.builder()
            .status(StatusCode.valueOf(context.session().status().name()))
            .sessionId(context.session().sessionId())
            .build();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
