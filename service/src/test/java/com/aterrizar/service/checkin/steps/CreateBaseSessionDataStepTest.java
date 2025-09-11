package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.init.SessionRequest;
import com.aterrizar.service.core.model.session.Session;
import com.neovisionaries.i18n.CountryCode;

@ExtendWith(MockitoExtension.class)
class CreateBaseSessionDataStepTest {

    @InjectMocks private CreateBaseSessionDataStep createBaseSessionDataStep;

    @Test
    void shouldExecuteWhenInitIsInstanceOfInitContextAndInitRequestIsValid() {
        var sessionRequest =
                SessionRequest.builder()
                        .countryCode(CountryCode.CO)
                        .passengers(2)
                        .flights(List.of("Flight1", "Flight2"))
                        .build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequest)).build();

        assertTrue(createBaseSessionDataStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenContextIsNotInitContext() {
        var context = Context.builder().build();
        assertFalse(createBaseSessionDataStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenSessionRequestIsNotPresent() {
        var context = InitContext.builder().sessionRequest(Optional.empty()).build();
        assertFalse(createBaseSessionDataStep.when(context));
    }

    @Test
    void shouldReturnFailureWhenPassengersAreLessThanOne() {
        var sessionRequest =
                SessionRequest.builder()
                        .flights(List.of("Flight1"))
                        .passengers(0)
                        .countryCode(CountryCode.AD)
                        .build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequest)).build();

        var result = createBaseSessionDataStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertEquals("Passengers.", result.message());
    }

    @Test
    void shouldReturnSuccessWhenSessionRequestIsValid() {
        var sessionRequest =
                SessionRequest.builder()
                        .flights(List.of("Flight1", "Flight2"))
                        .passengers(2)
                        .countryCode(CountryCode.AD)
                        .build();
        var context =
                InitContext.builder()
                        .sessionRequest(Optional.of(sessionRequest))
                        .session(Session.builder().build())
                        .build();

        var result = createBaseSessionDataStep.onExecute(context);

        assertTrue(result.isSuccess());
        var updatedContext = result.context();
        assertNotNull(updatedContext.session().sessionData());
        assertEquals(2, updatedContext.session().sessionData().passengers());
        assertEquals(CountryCode.AD, updatedContext.session().sessionData().countryCode());
    }
}
