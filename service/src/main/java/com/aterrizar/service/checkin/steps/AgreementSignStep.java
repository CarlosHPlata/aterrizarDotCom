package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgreementSignStep implements Step {
    @Override
    public StepResult onExecute(Context context) {
        if (isFieldFilled(context)) {
            var updatedContext = captureAgreementSignedField(context);
            return StepResult.success(updatedContext);
        }

        var updatedContext = requestAgreementSignedField(context);
        return StepResult.terminal(updatedContext);
    }

    @Override
    public boolean when(Context context) {
        if (context.session().sessionData() != null) {
            return !context.session().sessionData().agreementSigned();
        }

        return false;
    }

    private Context requestAgreementSignedField(Context context) {
        return context.withRequiredField(RequiredField.AGREEMENT_SIGNED);
    }

    private Context captureAgreementSignedField(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        return optionalRequest.map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.AGREEMENT_SIGNED))
                .map(agreementSigned -> {
                    var fieldValue = agreementSigned.equalsIgnoreCase("true");
                    return context.withSessionData(builder -> builder.agreementSigned(fieldValue));
                })
                .orElseThrow(() -> new IllegalStateException("Agreement Signed is missing in the request."));
    }

    private boolean isFieldFilled(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());
        return optionalRequest.isPresent()
                && optionalRequest.get().providedFields().get(RequiredField.AGREEMENT_SIGNED) != null;
    }
}
