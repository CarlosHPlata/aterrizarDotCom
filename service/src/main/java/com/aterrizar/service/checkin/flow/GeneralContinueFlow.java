package com.aterrizar.service.checkin.flow;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.steps.AgreementSignStep;
import com.aterrizar.service.checkin.steps.BiometricEnrollmentStep;
import com.aterrizar.service.checkin.steps.CompleteCheckinStep;
import com.aterrizar.service.checkin.steps.DigitalVisaValidationStep;
import com.aterrizar.service.checkin.steps.GetSessionStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.PaymentMethodStep;
import com.aterrizar.service.checkin.steps.PaymentValidationStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.checkin.steps.TransactionFinalizationStep;
import com.aterrizar.service.checkin.steps.ValidateSessionStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import com.aterrizar.service.core.model.ExperimentalStepKey;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GeneralContinueFlow implements FlowStrategy {
    private final AgreementSignStep agreementSignStep;
    private final CompleteCheckinStep completeCheckinStep;
    private final DigitalVisaValidationStep digitalVisaValidationStep;
    private final GetSessionStep getSessionStep;
    private final ValidateSessionStep validateSessionStep;
    private final BiometricEnrollmentStep biometricEnrollmentStep;
    private final PassportInformationStep passportInformationStep;
    private final PaymentMethodStep paymentMethodStep;
    private final PaymentValidationStep paymentValidationStep;
    private final SaveSessionStep saveSessionStep;
    private final TransactionFinalizationStep transactionFinalizationStep;
    
    @Override
    public FlowExecutor flow(FlowExecutor baseExecutor) {
        return baseExecutor
                .and(getSessionStep)
                .and(validateSessionStep)
                .andExperimental(biometricEnrollmentStep, ExperimentalStepKey.BIOMETRIC_CHECK)
                .and(passportInformationStep)
                .and(paymentMethodStep)
                .and(paymentValidationStep)
                .and(transactionFinalizationStep)
                .and(digitalVisaValidationStep)
                .andExperimental(agreementSignStep, ExperimentalStepKey.AGREEMENT_SIGN)
                .and(completeCheckinStep)
                .andFinally(saveSessionStep);
    }
}
