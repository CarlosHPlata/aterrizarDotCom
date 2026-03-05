package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.FieldType;
import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.external.BiometricGateway;

import lombok.RequiredArgsConstructor;

/**
 * BiometricEnrollmentStep handles the two-phase biometric authentication flow:
 * - Phase 1: Initiate session with gateway and request biometric verification field
 * - Phase 2: Verify the provided biometric data via gateway
 * 
 * Contract:
 * - SessionData attributes: biometricAuthenticated, biometricSessionToken
 * - Required field ID: authSessionToken + "_verified"
 * - Uses rawProvidedFields to read biometric values (keys can be unknown)
 * 
 * Flow logic:
 * - No token: call gateway.start(), save token, return USER_INPUT_REQUIRED
 * - Token exists: read rawProvidedFields[token+"_verified"]
 *   - Missing: request again
 *   - Exists: call gateway.verify(valor)
 *     - Success: set biometricAuthenticated=true, clear token
 *     - Failure (false or 406): set biometricAuthenticated=false, clear token, continue manual path
 */
@Service
@RequiredArgsConstructor
public class BiometricEnrollmentStep implements Step {

    private final BiometricGateway biometricGateway;

    @Override
    public boolean when(Context context) {
        return !context.session().isBiometricAuthenticated();
    }

    @Override
    public StepResult onExecute(Context context) {
        var storedToken = context.session().biometricSessionToken();

        // Phase 1: No token -> Initialize biometric session
        if (storedToken == null) {
            var authSessionToken = biometricGateway.startSession();
            var fieldId = authSessionToken + "_verified";
            var updatedContext = context
                    .withSession(b -> b.biometricSessionToken(authSessionToken))
                    .withRequiredField(fieldId, "Biometric Verification", FieldType.TEXT);
            return StepResult.terminal(updatedContext);
        }

        // Phase 2: Token exists -> Verify biometric data
        var fieldId = storedToken + "_verified";
        var biometricValue = Optional.ofNullable(context.checkinRequest())
                .map(CheckinRequest::rawProvidedFields)
                .map(fields -> fields.get(fieldId))
                .orElse(null);

        // Field not provided yet -> Request again
        if (biometricValue == null) {
            return StepResult.terminal(
                    context.withRequiredField(fieldId, "Biometric Verification", FieldType.TEXT));
        }

        // Field provided -> Verify via gateway
        boolean verified = biometricGateway.verifySession(storedToken);

        if (verified) {
            // Success: Set authenticated and clear token
            return StepResult.success(
                    context.withSession(b -> b
                            .biometricAuthenticated(true)
                            .biometricSessionToken(null)));
        }

        // Failure: Set not authenticated, clear token, and continue to manual path
        return StepResult.success(
                context.withSession(b -> b
                        .biometricAuthenticated(false)
                        .biometricSessionToken(null)));
    }
}
