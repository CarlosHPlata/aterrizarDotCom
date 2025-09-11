package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.external.FlightGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RetrieveFlightsDataStep implements Step {
    private final FlightGateway flightGateway;

    @Override
    public boolean when(Context context) {
        if (context instanceof InitContext initContext) {
            return initContext.sessionRequest().isPresent();
        }

        return false;
    }

    @Override
    public StepResult onExecute(Context context) {
        var initContext = (InitContext) context;
        var sessionRequest = initContext.sessionRequest().orElseThrow();

        if (sessionRequest.flights() == null || sessionRequest.flights().isEmpty()) {
            return StepResult.failure(context, "No flight codes provided.");
        }

        if (sessionRequest.flights().stream().anyMatch(flightCode -> flightCode.length() != 10)) {
            return StepResult.failure(context, "Invalid flight codes.");
        }

        var flights = flightGateway.getFlightData(sessionRequest.flights());
        var updatedContext = context.withSessionData(builder -> builder.flights(flights));

        return StepResult.success(updatedContext);
    }
}
