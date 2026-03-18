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

class RfcInputStepTest {

    private RfcInputStep rfcInputStep;

    @BeforeEach
    void setUp() {
        rfcInputStep = new RfcInputStep();
    }

    @Test
    void shouldExecuteWhenRfcIsNotSetInSession() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(null));

        var result = rfcInputStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenRfcIsAlreadySet() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc("ABCD840101XYZ"));

        var result = rfcInputStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldRequestRfcFieldWhenNotProvided() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(null));

        var stepResult = rfcInputStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(updatedContext.checkinResponse().providedFields().contains(RequiredField.RFC));
    }

    @Test
    void shouldCaptureRfcWhenProvided() {
        var rfc = "ABCD840101XYZ";

        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(null))
                        .withCheckinRequest(
                                requestBuilder ->
                                        requestBuilder.providedFields(
                                                Map.of(RequiredField.RFC, rfc)));

        var stepResult = rfcInputStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertEquals(rfc, updatedContext.userInformation().rfc());
    }
}
