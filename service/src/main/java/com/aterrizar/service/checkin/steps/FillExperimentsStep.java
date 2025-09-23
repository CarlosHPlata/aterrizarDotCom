package com.aterrizar.service.checkin.steps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.ExperimentalGateway;

@Service
@RequiredArgsConstructor
@Slf4j
public class FillExperimentsStep implements Step {

    private final ExperimentalGateway experimentalGateway;

    @Override
    public boolean when(Context context) {
        var userInfo = context.userInformation();

        if (userInfo == null) {
            return false;
        }
        var email = userInfo.email();
        return email != null;
    }

    @Override
    public StepResult onExecute(Context context) {
        var session = context.session();
        var userInfo = context.userInformation();
        var email = userInfo.email();

        var experimentalData = experimentalGateway.getActiveExperiments(email);
        log.info(
                "Session [{}] - Email [{}] - Experiments: {}",
                session.sessionId(),
                email,
                experimentalData);
        var updatedContext = context.withExperimentalData(experimentalData);
        return StepResult.success(updatedContext);
    }
}
