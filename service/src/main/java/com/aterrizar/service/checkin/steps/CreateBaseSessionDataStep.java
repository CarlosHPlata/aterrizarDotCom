package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.session.SessionData;
import com.aterrizar.service.external.FlightGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateBaseSessionDataStep implements Step {
    private final FlightGateway flightGateway;

    @Override
    public boolean when(Context context) {
        if (context instanceof InitContext initContext) {
            return initContext.sessionRequest().isPresent()
                    && !initContext.sessionRequest().get().flights().isEmpty();
        }
        return false;
    }

    @Override
    public StepResult onExecute(Context context) {
        var initContext = (InitContext) context;
        var sessionRequest = initContext.sessionRequest().orElseThrow();

        if (sessionRequest.passengers() < 1) {
            return StepResult.failure(context, "Passengers.");
        }

        var flights = flightGateway.getFlightData(sessionRequest.flights());
        var sessionData =
                SessionData.builder()
                        .countryCode(sessionRequest.countryCode())
                        .passengers(sessionRequest.passengers())
                        .flights(flights)
                        .build();

        return StepResult.success(
                context.withSession(context.session().withSessionData(sessionData)));
    }
}
