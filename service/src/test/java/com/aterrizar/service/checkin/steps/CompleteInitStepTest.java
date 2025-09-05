package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompleteInitStepTest {
    private CompleteInitStep completeInitStep;

    @BeforeEach
    void setUp() {
        completeInitStep = new CompleteInitStep();
    }

    @Test
    void testOnExecute() {
        Context initialContext = mock(Context.class);

        var result = completeInitStep.onExecute(initialContext);
        when(initialContext.withStatus(Status.INITIALIZED)).thenReturn(initialContext);

        assertTrue(result.isTerminal());
        assertTrue(result.isSuccess());
    }

}