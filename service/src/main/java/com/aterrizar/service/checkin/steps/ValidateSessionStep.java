package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidateSessionStep implements Step {
    @Override
    public StepResult onExecute(Context context) {
        var request = context.checkinRequest();

        if (!context.countryCode().equals(request.countryCode())) {
            return StepResult.failure(context, "Country code does not match session");
        }

        if (!context.userId().equals(request.userId())) {
            return StepResult.failure(context, "User ID does not match session");
        }

        return StepResult.success(context);
    }
}
