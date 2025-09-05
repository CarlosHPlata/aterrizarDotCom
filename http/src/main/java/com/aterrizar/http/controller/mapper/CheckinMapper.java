package com.aterrizar.http.controller.mapper;

import com.aterrizar.http.controller.mapper.fields.RequiredFieldMapper;
import com.aterrizar.http.dto.CheckinRequestData;
import com.aterrizar.http.dto.CheckinResponseData;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.request.CheckinRequest;
import org.springframework.http.ResponseEntity;
import org.yaml.snakeyaml.util.Tuple;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckinMapper implements RequestMapperStrategy<Context, CheckinResponseData, CheckinRequestData> {
    @Override
    public Context mapRequestToContext(CheckinRequestData checkinRequestData) {
        var fields = Optional.ofNullable(checkinRequestData.getProvidedFields())
                .orElse(Collections.emptyMap())
                .entrySet()
                .stream()
                .map(entry -> RequiredFieldMapper.map(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Tuple::_1, Tuple::_2));

        var request = CheckinRequest.builder()
                .countryCode(mapCountry(checkinRequestData.getCountry()))
                .sessionId(checkinRequestData.getSessionId())
                .userId(checkinRequestData.getUserId())
                .providedFields(fields)
                .build();

        return Context.builder()
                .checkinRequest(request)
                .build();
    }

    @Override
    public ResponseEntity<CheckinResponseData> mapContextToResponse(Context context) {
        var mapper = StatusMapperFactory.getMapperByStatusCode(context.status().name());
        var response = mapper.map(context);
        return new ResponseEntity<>(response, mapper.getHttpStatus());
    }
}
