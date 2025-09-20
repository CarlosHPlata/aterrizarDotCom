package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.config.DigitalVisaProperties;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

import lombok.RequiredArgsConstructor;

/**
 * Step responsible for validating digital visa requirements during check-in.
 *
 * <p>This step checks if the passenger is traveling to countries that require digital visa
 * validation and ensures the visa number is provided when needed.
 */
@Service
@RequiredArgsConstructor
public class DigitalVisaValidationStep implements Step {

    private final DigitalVisaProperties digitalVisaProperties;

    @Override
    public StepResult onExecute(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        if (isFieldFilled(optionalRequest)) {
            var updatedContext = captureVisaNumber(context);
            return StepResult.success(updatedContext);
        }

        var updatedContext = requestVisaNumber(context);
        return StepResult.terminal(updatedContext);
    }

    @Override
    public boolean when(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();
        var sessionData = session.sessionData();

        if (Optional.ofNullable(userInfo).map(user -> user.visaNumber()).isPresent()) {
            return false;
        }

        return requiresDigitalVisa(sessionData);
    }

    /**
     * Checks if any flight destination requires digital visa validation.
     *
     * @param sessionData the session data containing flights
     * @return true if digital visa is required for any destination
     */
    private boolean requiresDigitalVisa(
            com.aterrizar.service.core.model.session.SessionData sessionData) {
        if (sessionData == null || sessionData.flights() == null) {
            return false;
        }

        return sessionData.flights().stream()
                .anyMatch(
                        flight ->
                                digitalVisaProperties.isDigitalVisaRequired(
                                        flight.destination().countryCode().name()));
    }

    /**
     * Captures the visa number from the request and updates the context.
     *
     * @param context the current context
     * @return updated context with visa number
     */
    private Context captureVisaNumber(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        return optionalRequest
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.VISA_NUMBER))
                .map(
                        visaNumber ->
                                context.withUserInformation(
                                        builder -> builder.visaNumber(visaNumber)))
                .orElseThrow(
                        () -> new IllegalStateException("Visa number is missing in the request."));
    }

    /**
     * Checks if the visa number is provided in the request.
     *
     * @param optionalRequest the optional checkin request
     * @return true if visa number is provided
     */
    private static boolean isFieldFilled(Optional<CheckinRequest> optionalRequest) {
        return optionalRequest.isPresent()
                && optionalRequest.get().providedFields().get(RequiredField.VISA_NUMBER) != null;
    }

    /**
     * Requests the visa number by adding it to required fields.
     *
     * @param context the current context
     * @return updated context with visa number as required field
     */
    private Context requestVisaNumber(Context context) {
        return context.withRequiredField(RequiredField.VISA_NUMBER);
    }
}
