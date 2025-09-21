package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

@Service
public class FundsCheckStep implements Step {

    @Override
    public boolean when(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();

        return Optional.ofNullable(userInfo).isPresent()
                && Optional.ofNullable(userInfo.usFunds()).isEmpty();
    }

    @Override
    public StepResult onExecute(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        if (isFieldFilled(optionalRequest)) {
            var updatedContext = captureFunds(context);
            return StepResult.success(updatedContext);
        }

        var updatedContext = requestFunds(context);
        return StepResult.terminal(updatedContext);
    }

    private Context captureFunds(Context context) {
        var optionalRequest = Optional.ofNullable(context.checkinRequest());

        return optionalRequest
                .map(CheckinRequest::providedFields)
                .map(fields -> fields.get(RequiredField.FUNDS_AMOUNT_US))
                .map(this::validateFunds)
                .map(funds -> context.withUserInformation(builder -> builder.usFunds(funds)))
                .orElseThrow(
                        () -> new IllegalStateException("US funds are missing in the request."));
    }

    private double validateFunds(String fundsStr) {
        Double funds = Double.parseDouble(fundsStr);
        if (funds < 0) {
            throw new IllegalArgumentException("Funds amount must be a positive number");
        }
        return funds;
    }

    private static boolean isFieldFilled(Optional<CheckinRequest> optionalRequest) {
        return optionalRequest.isPresent()
                && optionalRequest.get().providedFields().get(RequiredField.FUNDS_AMOUNT_US)
                        != null;
    }

    private Context requestFunds(Context context) {
        return context.withRequiredField(RequiredField.FUNDS_AMOUNT_US);
    }
}
