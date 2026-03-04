package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.BiometricGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class BiometricEnrollmentStepTest {

    private BiometricGateway biometricGateway;
    private BiometricEnrollmentStep biometricEnrollmentStep;

    @BeforeEach
    void setUp() {
        biometricGateway = mock(BiometricGateway.class);
        biometricEnrollmentStep = new BiometricEnrollmentStep(biometricGateway);
    }

    @Test
    void shouldExecuteWhenNotBiometricAuthenticated() {
        var context = MockContext.initializedMock(CountryCode.US);

        assertTrue(biometricEnrollmentStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenAlreadyBiometricAuthenticated() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricAuthenticated(true));

        assertFalse(biometricEnrollmentStep.when(context));
    }

    @Test
    void shouldStartBiometricSessionAndAskForVerifiedFieldOnFirstExecution() {
        var token = "session_token_abc";
        when(biometricGateway.startSession()).thenReturn(token);

        var context = MockContext.initializedMock(CountryCode.US);
        var result = biometricEnrollmentStep.onExecute(context);

        assertTrue(result.isTerminal());
        assertTrue(result.isSuccess());
        assertEquals(token, result.context().session().biometricSessionToken());
        assertTrue(
                result.context()
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.BIOMETRIC_VERIFIED));
    }

    @Test
    void shouldAskForVerifiedFieldAgainWhenTokenStoredButFieldNotProvided() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken("existing_token"));

        var result = biometricEnrollmentStep.onExecute(context);

        assertTrue(result.isTerminal());
        assertTrue(result.isSuccess());
        verify(biometricGateway, never()).startSession();
        assertTrue(
                result.context()
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.BIOMETRIC_VERIFIED));
    }

    @Test
    void shouldVerifyAndMarkAuthenticatedWhenBiometricSucceeds() {
        var token = "existing_token";
        when(biometricGateway.verifySession(token)).thenReturn(true);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken(token))
                        .withCheckinRequest(
                                b ->
                                        b.providedFields(
                                                Map.of(RequiredField.BIOMETRIC_VERIFIED, token)));

        var result = biometricEnrollmentStep.onExecute(context);

        assertFalse(result.isTerminal());
        assertTrue(result.isSuccess());
        assertTrue(result.context().session().isBiometricAuthenticated());
    }

    @Test
    void shouldReturnFailureWhenBiometricVerificationFails() {
        var token = "existing_token_1";
        when(biometricGateway.verifySession(token)).thenReturn(false);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken(token))
                        .withCheckinRequest(
                                b ->
                                        b.providedFields(
                                                Map.of(RequiredField.BIOMETRIC_VERIFIED, token)));

        var result = biometricEnrollmentStep.onExecute(context);

        assertTrue(result.isTerminal());
        assertFalse(result.isSuccess());
        assertEquals("Biometric verification failed", result.message());
    }
}
