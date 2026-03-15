package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.external.BiometricGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BiometricEnrollmentStep implements Step {

    private final BiometricGateway biometricGateway;

    @Override
    public boolean when(Context context) {
        return !context.session().isBiometricAuthenticated()
            && !context.session().isBiometricFailed();
            
    }

    @Override
    public StepResult onExecute(Context context) {
        var storedToken = context.session().biometricSessionToken();

        if (storedToken == null) {
            var authSessionToken = biometricGateway.startSession();
            var updatedContext =
                    context.withSession(b -> b.biometricSessionToken(authSessionToken))
                            .withRequiredField(RequiredField.BIOMETRIC_VERIFIED);
            return StepResult.terminal(updatedContext);
        }

        var providedFields =
                Optional.ofNullable(context.checkinRequest())
                        .map(CheckinRequest::providedFields)
                        .orElse(null);

        if (providedFields == null
                || !providedFields.containsKey(RequiredField.BIOMETRIC_VERIFIED)) {
            return StepResult.terminal(context.withRequiredField(RequiredField.BIOMETRIC_VERIFIED));
        }

        String userProvidedToken = providedFields.get(RequiredField.BIOMETRIC_VERIFIED);
        boolean verified = biometricGateway.verifySession(userProvidedToken);
        if (verified) {
            return StepResult.success(context.withSession(b -> b.biometricAuthenticated(true)));
        }

        return StepResult.success(context.withSession(b -> b
        .biometricAuthenticated(false)
        .biometricFailed(true)
        .biometricSessionToken(null)));
    }
}
