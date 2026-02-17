package com.aterrizar.service.external;

import com.aterrizar.service.core.model.session.ExperimentalData;

public interface ExperimentalGateway {
    ExperimentalData getActiveExperiments(String email);
}
