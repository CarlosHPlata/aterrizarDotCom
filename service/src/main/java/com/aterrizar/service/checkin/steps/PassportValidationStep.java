package com.aterrizar.service.checkin.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.external.PassportGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PassportValidationStep implements Step {
    private static final Logger logger = LoggerFactory.getLogger(PassportValidationStep.class);
    private final PassportGateway passportGateway;

    @Override
    public boolean when(Context context) {
        if (context instanceof InitContext initContext) {
            return initContext.sessionRequest().isPresent();
        }

        return false;
    }

    @Override
    public StepResult onExecute(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();
        String passportNumber = userInfo != null ? userInfo.passportNumber() : null;
        logger.info("Validating passport number: {}", passportNumber);

        if (passportNumber == null || passportNumber.isBlank()) {
            return StepResult.failure(context, "Passport number is required.");
        }

        boolean isValid = passportGateway.validate(passportNumber);
        var updatedContext =
                context.withSessionData(builder -> builder.passportValidationCleared(isValid));
        if (isValid) {
            return StepResult.success(updatedContext);
        } else {
            return StepResult.failure(updatedContext, "Invalid passport number");
        }
    }
}
