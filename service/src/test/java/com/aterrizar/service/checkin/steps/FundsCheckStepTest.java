package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

public class FundsCheckStepTest {

    private FundsCheckStep fundsCheckStep;

    @BeforeEach
    void setUp() {
        fundsCheckStep = new FundsCheckStep();
    }

    @Test
    void shouldCaptureFundsWhenProvided() {
        var fundsAdded = 22345;

        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.passportNumber(null))
                        .withCheckinRequest(
                                requestBuilder ->
                                        requestBuilder.providedFields(
                                                Map.of(
                                                        RequiredField.FUNDS_AMOUNT_US,
                                                        String.valueOf(fundsAdded))));

        var stepResult = fundsCheckStep.onExecute(context);
        var updatedContext = stepResult.context();
        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertEquals(fundsAdded, updatedContext.userInformation().usFunds());
    }
}
