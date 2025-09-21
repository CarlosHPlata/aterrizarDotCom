package com.aterrizar.service.checkin.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.ExperimentalGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FillExperimentsStep implements Step {

    private final ExperimentalGateway experimentalGateway;
    private final String EXPERIMENTAL_EMAIL_DOMAIN = "@checkin.com";

    private final Logger logger = LoggerFactory.getLogger(FillExperimentsStep.class);

    @Override
    public boolean when(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();
        if (userInfo == null) {
            return false;
        }
        var email = userInfo.email();
        return email != null;
    }

    @Override
    public StepResult onExecute(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();
        var email = userInfo.email();

        if (!isExperimentalEmail(email)) {
            return StepResult.success(context);
        }

        var experimentalData = experimentalGateway.getActiveExperiments(email);
        logger.info(
                "Session [{}] - Email [{}] - Experiments: {}",
                session.sessionId(),
                email,
                experimentalData);
        var updatedContext = context.withExperimentalData(experimentalData);
        return StepResult.success(updatedContext);
    }

    private boolean isExperimentalEmail(String email) {
        return email != null && email.endsWith(EXPERIMENTAL_EMAIL_DOMAIN);
    }
}
