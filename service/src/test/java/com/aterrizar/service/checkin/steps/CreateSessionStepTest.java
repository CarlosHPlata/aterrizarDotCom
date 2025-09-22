package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.init.SessionRequest;
import com.aterrizar.service.core.model.session.Status;

class CreateSessionStepTest {
    private CreateBaseSessionStep createSessionStep;

    @BeforeEach
    void setUp() {
        createSessionStep = new CreateBaseSessionStep();
    }

    @Test
    void testStepShouldBeExecutedWhenIsInitContextAndIsInitRequest() {
        var sessionRequest = SessionRequest.builder().build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequest)).build();

        assertTrue(createSessionStep.when(context));
    }

    @Test
    void stepShouldNotExecuteWhenIsNotInitContext() {
        var context = Context.builder().build();

        assertFalse(createSessionStep.when(context));
    }

    @Test
    void stepShouldNotExecuteWhenIsInitContextButIsNotInitRequest() {
        var context = InitContext.builder().build();

        assertFalse(createSessionStep.when(context));
    }

    @Test
    void testOnExecuteShouldCreateNewValidSession() {
        var userId = UUID.randomUUID();
        var sessionRequest = SessionRequest.builder().userId(userId).build();
        var context = InitContext.builder().sessionRequest(Optional.of(sessionRequest)).build();

        var result = createSessionStep.onExecute(context);
        var updatedContext = result.context();

        assertTrue(result.isSuccess());
        assertNotNull(updatedContext.session().userInformation());
        assertEquals(userId, updatedContext.session().userInformation().userId());
        assertEquals(Status.INITIALIZED, updatedContext.session().status());
        assertNotNull(updatedContext.session().sessionId());
    }
}
