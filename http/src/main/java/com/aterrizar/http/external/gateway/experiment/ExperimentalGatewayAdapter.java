package com.aterrizar.http.external.gateway.experiment;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.model.session.ExperimentalData;
import com.aterrizar.service.external.ExperimentalGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExperimentalGatewayAdapter implements ExperimentalGateway {
    private final ExperimentHttpClient experimentHttpClient;

    @Override
    public ExperimentalData getActiveExperiments(String email) {
        var experimentsDto = experimentHttpClient.getExperiments(email);
        return ExperimentalData.builder().experiments(experimentsDto.experiments()).build();
    }
}
