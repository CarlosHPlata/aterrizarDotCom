package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void shouldExecuteWhenFundsAreNotSetInSession() {
        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(null));

        var result = fundsCheckStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenFundsAreSet() {
        var fundsAdded = 22345.0;
        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(fundsAdded));

        var result = fundsCheckStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldRequestFundsAmountWhenNotSet() {
        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(null));

        var stepResult = fundsCheckStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertTrue(stepResult.isTerminal());
        assertTrue(
                updatedContext
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.FUNDS_AMOUNT_US));
    }

    @Test
    void shouldCaptureFundsWhenProvided() {
        var fundsAdded = 22345;

        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(null))
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

    @Test
    void shouldThrowExceptionWhenFundsAreNegative() {
        var fundsAdded = -22345;

        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(null))
                        .withCheckinRequest(
                                requestBuilder ->
                                        requestBuilder.providedFields(
                                                Map.of(
                                                        RequiredField.FUNDS_AMOUNT_US,
                                                        String.valueOf(fundsAdded))));

        assertThrows(IllegalArgumentException.class, () -> fundsCheckStep.onExecute(context));
    }

    @Test
    void shouldThrowExceptionWhenFundsAreNotNumeric() {
        var fundsAdded = "Doscientos";

        var context =
                MockContext.initializedMock(CountryCode.VE)
                        .withUserInformation(builder -> builder.usFunds(null))
                        .withCheckinRequest(
                                requestBuilder ->
                                        requestBuilder.providedFields(
                                                Map.of(
                                                        RequiredField.FUNDS_AMOUNT_US,
                                                        String.valueOf(fundsAdded))));

        assertThrows(IllegalArgumentException.class, () -> fundsCheckStep.onExecute(context));
    }
}
