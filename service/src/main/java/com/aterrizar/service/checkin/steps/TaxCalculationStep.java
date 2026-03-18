package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.TaxGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaxCalculationStep implements Step {

    private final TaxGateway taxGateway;

    @Override
    public StepResult onExecute(Context context) {
        var rfc = context.userInformation().rfc();

        try {
            var taxData = taxGateway.getTax(rfc);
            var updatedContext =
                    context.withUserInformation(builder -> builder.taxAmount(taxData.taxAmount()));
            return StepResult.success(updatedContext);
        } catch (RuntimeException e) {
            return StepResult.failure(context, "Invalid RFC");
        }
    }

    @Override
    public boolean when(Context context) {
        return Optional.ofNullable(context.session().userInformation())
                .map(info -> info.rfc() != null && info.taxAmount() == null)
                .orElse(false);
    }
}
