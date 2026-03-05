package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

// Test class for verifying skip logic of AgreementSignStep when biometric
// authentication is activated.

class AgreementSignStepBiometricSkipTest {

    private AgreementSignStep agreementSignStep;

    @BeforeEach
    void setUp() {
        agreementSignStep = new AgreementSignStep();
    }

    @Test
    void shouldNotExecuteWhenUserIsBiometricAuthenticated() {

        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withSessionData(builder -> builder.agreementSigned(false))
                        .withSession(sessionBuilder -> sessionBuilder.biometricAuthenticated(true));

        // When: The step's when() condition is evaluated
        var result = agreementSignStep.when(context);

        // Then: The step should NOT execute (returns false)
        assertFalse(result);
    }

    @Test
    void shouldExecuteWhenUserIsNotBiometricAuthenticatedAndAgreementNotSigned() {

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSessionData(builder -> builder.agreementSigned(false))
                        .withSession(
                                sessionBuilder -> sessionBuilder.biometricAuthenticated(false));

        // When: The step's when() condition is evaluated
        var result = agreementSignStep.when(context);

        // Then: The step SHOULD execute (returns true)
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenBiometricAuthenticatedEvenIfAgreementNotSigned() {

        var context =
                MockContext.initializedMock(CountryCode.CO)
                        .withSessionData(builder -> builder.agreementSigned(false))
                        .withSession(sessionBuilder -> sessionBuilder.biometricAuthenticated(true));

        // When: The step's when() condition is evaluated
        var result = agreementSignStep.when(context);

        // The step should NOT execute (returns false)
        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenAgreementIsAlreadySigned() {

        var context =
                MockContext.initializedMock(CountryCode.IT)
                        .withSessionData(builder -> builder.agreementSigned(true))
                        .withSession(
                                sessionBuilder -> sessionBuilder.biometricAuthenticated(false));

        var result = agreementSignStep.when(context);

        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenBiometricAuthenticatedAndAgreementAlreadySigned() {

        var context =
                MockContext.initializedMock(CountryCode.ES)
                        .withSessionData(builder -> builder.agreementSigned(true))
                        .withSession(sessionBuilder -> sessionBuilder.biometricAuthenticated(true));

        var result = agreementSignStep.when(context);

        assertFalse(result);
    }
}
