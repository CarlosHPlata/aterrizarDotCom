package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.init.SessionRequest;
import com.aterrizar.service.core.model.session.FlightData;
import com.aterrizar.service.external.FlightGateway;

@ExtendWith(MockitoExtension.class)
class RetrieveFlightsDataStepTest {
    @Mock private FlightGateway flightGateway;
    @InjectMocks private RetrieveFlightsDataStep retrieveFlightsDataStep;

    @Test
    void shouldExecuteWhenNoFlightDataIsInSessionButInRequest() {
        var sessionRequests =
                SessionRequest.builder().flights(List.of("USJFKGBLHF", "GBLHRMXMID")).build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequests)).build();

        assertTrue(retrieveFlightsDataStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenNoFlightDataIsInRequest() {
        var context = InitContext.builder().sessionRequest(Optional.empty()).build();

        assertFalse(retrieveFlightsDataStep.when(context));
    }

    @Test
    void shouldRejectAndValidateFlightsAreStringOfLength10() {
        var sessionRequests =
                SessionRequest.builder()
                        .flights(List.of("USJFKGBLHF", "GBLHRMXMID", "INVALIDFLIGHTCODE"))
                        .build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequests)).build();

        var result = retrieveFlightsDataStep.onExecute(context);
        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertEquals("Invalid flight codes.", result.message());
    }

    @Test
    void shouldRejectIfNoFlightDataIsSent() {
        var sessionRequests = SessionRequest.builder().flights(List.of()).build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequests)).build();

        var result = retrieveFlightsDataStep.onExecute(context);
        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertEquals("No flight codes provided.", result.message());
    }

    @Test
    void shouldFillFlightDataWhenFlightCodesAreValid() {
        var flights = List.of("USJFKGBLHF");
        var flightsData = List.of(FlightData.builder().flightNumber("USJFKGBLHF").build());
        var sessionRequests = SessionRequest.builder().flights(flights).build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequests)).build();

        when(flightGateway.getFlightData(flights)).thenReturn(flightsData);

        var result = retrieveFlightsDataStep.onExecute(context);
        var updatedContext = result.context();
        assertTrue(result.isSuccess());
        assertFalse(result.isTerminal());
        assertEquals(flightsData, updatedContext.session().sessionData().flights());
    }
}
