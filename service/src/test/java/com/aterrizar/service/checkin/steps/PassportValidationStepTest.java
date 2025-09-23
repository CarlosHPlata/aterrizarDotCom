package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

public class PassportValidationStepTest {

    private PassportValidationStep passportValidationStep;

    @BeforeEach
    public void setUp() {
        passportValidationStep = new PassportValidationStep(null);
    }

    @Test
    void shouldExecuteWhenPassportIsSet() {
        var context = MockContext.initializedMock(CountryCode.AD)
                .withUserInformation(builder -> builder.passportNumber("123456789").fullName("John Doe"));

        var result = passportValidationStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenPassportIsNotSet() {
        var context = MockContext.initializedMock(CountryCode.AD)
                .withUserInformation(builder -> builder.passportNumber(null));

        var result = passportValidationStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldFailWhenPassportIsBlank() {
        var context = MockContext.initializedMock(CountryCode.AD)
                .withUserInformation(builder -> builder.passportNumber("   ").fullName("John Doe"));

        var stepResult = passportValidationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertFalse(stepResult.isSuccess());
        assertEquals("Passport number is required.", stepResult.message());
        assertFalse(updatedContext.session().sessionData().passportValidationCleared());
    }

    @Test
    void shouldFailWhenPassportIsInvalid() {
        var context = MockContext.initializedMock(CountryCode.AD)
                .withUserInformation(builder -> builder.passportNumber("P7399").fullName("John Doe"));

        var stepResult = passportValidationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertFalse(stepResult.isSuccess());
        assertEquals("Invalid passport number", stepResult.message());
        assertFalse(updatedContext.session().sessionData().passportValidationCleared());
    }

    @Test
    void shouldSucceedWhenPassportIsValid() {
        var context = MockContext.initializedMock(CountryCode.AD)
                .withUserInformation(builder -> builder.passportNumber("G3567").fullName("John Doe"));

        var stepResult = passportValidationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertTrue(updatedContext.session().sessionData().passportValidationCleared());
    }
}
