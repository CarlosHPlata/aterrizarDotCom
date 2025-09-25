package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.feature.HealthDeclarationCountryChecker;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HealthDeclarationStep implements Step {
    private final HealthDeclarationCountryChecker countryChecker;

    @Override
    public StepResult onExecute(Context context) {
        if (!isHealthClearAcknowledgementFieldFilled(context)) {
            var updatedContext = requestHealthClearAcknowledgementField(context);
            return StepResult.terminal(updatedContext);
        }

        var updatedContext = captureHealthClearAcknowledgement(context);
        if (!updatedContext.session().sessionData().healthClearAcknowledgement()) {
            return StepResult.failure(updatedContext, "Health clear acknowledgement is required");
        }

        return StepResult.success(updatedContext);
    }

    @Override
    public boolean when(Context context) {
        var flights = context.session().sessionData().flights();
        return countryChecker.hasFlightsRequiringHealthDeclaration(flights);
    }

    private Context requestHealthClearAcknowledgementField(Context context) {
        return context.withRequiredField(RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT);
    }

    private Context captureHealthClearAcknowledgement(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        return optionalRequest
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT))
                .map(
                        healthClearAcknowledgement -> {
                            var fieldValue = healthClearAcknowledgement.equalsIgnoreCase("true");
                            return context.withSessionData(
                                    builder -> builder.healthClearAcknowledgement(fieldValue));
                        })
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Health clear acknowledgement is missing in the request."));
    }

    private boolean isHealthClearAcknowledgementFieldFilled(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());
        return optionalRequest.isPresent()
                && optionalRequest
                                .get()
                                .providedFields()
                                .get(RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT)
                        != null;
    }
}
