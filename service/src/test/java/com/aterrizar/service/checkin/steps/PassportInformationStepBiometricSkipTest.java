package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

/*
Test class for verifying skip logic of PassportInformationStep when biometric authentication
is activated.
 */
class PassportInformationStepBiometricSkipTest {

    private PassportInformationStep passportInformationStep;

    @BeforeEach
    void setUp() {
        passportInformationStep = new PassportInformationStep();
    }

    @Test
    void shouldNotExecuteWhenUserIsBiometricAuthenticated() {

        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withUserInformation(builder -> builder.passportNumber(null))
                        .withSession(sessionBuilder -> sessionBuilder.biometricAuthenticated(true));

        // The step's when() condition is evaluated
        var result = passportInformationStep.when(context);

        // The step should NOT execute (returns false)
        assertFalse(result);
    }

    @Test
    void shouldExecuteWhenUserIsNotBiometricAuthenticatedAndPassportNotSet() {

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.passportNumber(null))
                        .withSession(
                                sessionBuilder -> sessionBuilder.biometricAuthenticated(false));

        // The step's when() condition is evaluated
        var result = passportInformationStep.when(context);

        // The step SHOULD execute (returns true)
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenBiometricAuthenticatedEvenIfPassportNotSet() {

        var context =
                MockContext.initializedMock(CountryCode.CO)
                        .withUserInformation(builder -> builder.passportNumber(null))
                        .withSession(sessionBuilder -> sessionBuilder.biometricAuthenticated(true));

        // The step's when() condition is evaluated
        var result = passportInformationStep.when(context);

        // The step should NOT execute because biometric takes precedence
        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenPassportIsAlreadySet() {

        var context =
                MockContext.initializedMock(CountryCode.IT)
                        .withUserInformation(builder -> builder.passportNumber("AB123456"))
                        .withSession(
                                sessionBuilder -> sessionBuilder.biometricAuthenticated(false));

        // The step's when() condition is evaluated
        var result = passportInformationStep.when(context);

        // The step should NOT execute (passport already captured)
        assertFalse(result);
    }
}
