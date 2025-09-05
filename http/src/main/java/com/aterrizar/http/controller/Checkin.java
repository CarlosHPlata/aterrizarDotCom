package com.aterrizar.http.controller;

import com.aterrizar.http.api.CheckinApi;
import com.aterrizar.http.controller.mapper.CheckinMapper;
import com.aterrizar.http.controller.mapper.InitMapper;
import com.aterrizar.http.dto.*;
import com.aterrizar.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Checkin implements CheckinApi {
    private final CheckinService checkinService;

    @Override
    public ResponseEntity<InitResponseData> initCheckin(InitRequestData initRequestData) throws Exception {
        var initMapper = new InitMapper();
        var context = initMapper.mapRequestToContext(initRequestData);
        var updatedContext = checkinService.init(context, initMapper.mapCountry((initRequestData.getCountry())));

        return initMapper.mapContextToResponse(updatedContext);
    }

    @Override
    public ResponseEntity<CheckinResponseData> continueCheckin(CheckinRequestData checkinRequestData) throws Exception {
        var mapper = new CheckinMapper();
        var context = mapper.mapRequestToContext(checkinRequestData);
        var updatedContext = checkinService.checkin(context, mapper.mapCountry(checkinRequestData.getCountry()));

        return mapper.mapContextToResponse(updatedContext);
    }
}
