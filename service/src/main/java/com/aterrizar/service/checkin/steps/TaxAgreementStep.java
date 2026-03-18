package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

@Service
public class TaxAgreementStep implements Step {

    @Override
    public StepResult onExecute(Context context) {
        if (isFieldFilled(context)) {
            var updatedContext = captureDigitalSign(context);
            return StepResult.success(updatedContext);
        }

        var taxAmount = context.userInformation().taxAmount();
        var updatedContext = context.withRequiredField(RequiredField.DIGITAL_SIGN);
        if (taxAmount != null) {
            updatedContext =
                    updatedContext.withCheckinResponse(
                            builder ->
                                    builder.errorMessage("Tax amount to be signed: " + taxAmount));
        }
        return StepResult.terminal(updatedContext);
    }

    @Override
    public boolean when(Context context) {
        return Optional.ofNullable(context.session().userInformation())
                .map(
                        info ->
                                info.taxAmount() != null
                                        && (info.digitalSign() == null || !info.digitalSign()))
                .orElse(false);
    }

    private boolean isFieldFilled(Context context) {
        return Optional.ofNullable(context.checkinRequest())
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.DIGITAL_SIGN))
                .map(value -> value.equalsIgnoreCase("true"))
                .orElse(false);
    }

    private Context captureDigitalSign(Context context) {
        return Optional.ofNullable(context.checkinRequest())
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.DIGITAL_SIGN))
                .map(
                        value ->
                                context.withUserInformation(
                                        builder ->
                                                builder.digitalSign(
                                                        value.equalsIgnoreCase("true"))))
                .orElseThrow(
                        () -> new IllegalStateException("Digital Sign is missing in the request."));
    }
}
