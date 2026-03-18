package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

@Service
public class RfcInputStep implements Step {

    @Override
    public StepResult onExecute(Context context) {
        if (isFieldFilled(context)) {
            var updatedContext = captureRfc(context);
            return StepResult.success(updatedContext);
        }

        var updatedContext = context.withRequiredField(RequiredField.RFC);
        return StepResult.terminal(updatedContext);
    }

    @Override
    public boolean when(Context context) {
        return Optional.ofNullable(context.session().userInformation())
                .map(info -> info.rfc() == null)
                .orElse(false);
    }

    private boolean isFieldFilled(Context context) {
        return Optional.ofNullable(context.checkinRequest())
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.RFC))
                .isPresent();
    }

    private Context captureRfc(Context context) {
        return Optional.ofNullable(context.checkinRequest())
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.RFC))
                .map(rfc -> context.withUserInformation(builder -> builder.rfc(rfc)))
                .orElseThrow(() -> new IllegalStateException("RFC is missing in the request."));
    }
}
