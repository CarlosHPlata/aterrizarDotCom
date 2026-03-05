package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.external.BiometricGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

/**
 * Test suite for BiometricEnrollmentStep following BIO-4 ticket requirements:
 * - Phase 1: Initiate session and request dynamic required field (token + "_verified")
 * - Phase 2: Verify biometric via gateway using rawProvidedFields
 * - On success: set biometricAuthenticated=true, clear token
 * - On failure: set biometricAuthenticated=false, clear token, continue to manual path
 */
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
    void shouldStartBiometricSessionAndAskForDynamicVerifiedFieldOnFirstExecution() {
        var token = "session_token_abc";
        when(biometricGateway.startSession()).thenReturn(token);

        var context = MockContext.initializedMock(CountryCode.US);
        var result = biometricEnrollmentStep.onExecute(context);

        // Phase 1: Should be terminal (waiting for user input)
        assertTrue(result.isTerminal());
        assertTrue(result.isSuccess());
        assertEquals(token, result.context().session().biometricSessionToken());
        
        // Should contain dynamic required field with id = token + "_verified"
        var expectedFieldId = token + "_verified";
        var inputFields = result.context().checkinResponse().inputRequiredFields();
        assertTrue(inputFields.stream().anyMatch(field -> field.id().equals(expectedFieldId)),
                "Should have dynamic required field with id: " + expectedFieldId);
    }

    @Test
    void shouldAskForVerifiedFieldAgainWhenTokenStoredButFieldNotProvided() {
        var token = "existing_token";
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken(token));

        var result = biometricEnrollmentStep.onExecute(context);

        // Should ask for field again
        assertTrue(result.isTerminal());
        assertTrue(result.isSuccess());
        verify(biometricGateway, never()).startSession();
        
        // Should have dynamic field with id = token + "_verified"
        var expectedFieldId = token + "_verified";
        var inputFields = result.context().checkinResponse().inputRequiredFields();
        assertTrue(inputFields.stream().anyMatch(field -> field.id().equals(expectedFieldId)),
                "Should request dynamic field with id: " + expectedFieldId);
    }

    @Test
    void shouldVerifyAndMarkAuthenticatedWhenBiometricSucceeds() {
        var token = "existing_token";
        var fieldId = token + "_verified";
        var biometricValue = "biometric_data_123";
        
        when(biometricGateway.verifySession(token)).thenReturn(true);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken(token))
                        .withCheckinRequest(
                                b -> b.rawProvidedFields(Map.of(fieldId, biometricValue)));

        var result = biometricEnrollmentStep.onExecute(context);

        // Should succeed and continue to next step (not terminal)
        assertFalse(result.isTerminal());
        assertTrue(result.isSuccess());
        assertTrue(result.context().session().isBiometricAuthenticated());
        // Token should be cleared after successful verification
        assertNull(result.context().session().biometricSessionToken());
    }

    @Test
    void shouldNotAuthenticateButContinueWhenBiometricVerificationFails() {
        var token = "existing_token_1";
        var fieldId = token + "_verified";
        var biometricValue = "invalid_biometric_data";
        
        when(biometricGateway.verifySession(token)).thenReturn(false);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(b -> b.biometricSessionToken(token))
                        .withCheckinRequest(
                                b -> b.rawProvidedFields(Map.of(fieldId, biometricValue)));

        var result = biometricEnrollmentStep.onExecute(context);

        // Should succeed and continue to manual path (NOT terminal/failure)
        // This allows the flow to continue without getting stuck
        assertFalse(result.isTerminal());
        assertTrue(result.isSuccess());
        assertFalse(result.context().session().isBiometricAuthenticated());
        // Token should be cleared even on failure
        assertNull(result.context().session().biometricSessionToken());
    }
}
