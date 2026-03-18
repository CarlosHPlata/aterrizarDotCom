package com.aterrizar.service.checkin.flow;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.steps.CompleteCheckinStep;
import com.aterrizar.service.checkin.steps.GetSessionStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.PaymentMethodStep;
import com.aterrizar.service.checkin.steps.PaymentValidationStep;
import com.aterrizar.service.checkin.steps.RfcInputStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.checkin.steps.TaxAgreementStep;
import com.aterrizar.service.checkin.steps.TaxCalculationStep;
import com.aterrizar.service.checkin.steps.TransactionFinalizationStep;
import com.aterrizar.service.checkin.steps.ValidateSessionStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MxContinueFlow implements FlowStrategy {
    private final CompleteCheckinStep completeCheckinStep;
    private final GetSessionStep getSessionStep;
    private final PassportInformationStep passportInformationStep;
    private final PaymentMethodStep paymentMethodStep;
    private final PaymentValidationStep paymentValidationStep;
    private final RfcInputStep rfcInputStep;
    private final SaveSessionStep saveSessionStep;
    private final TaxAgreementStep taxAgreementStep;
    private final TaxCalculationStep taxCalculationStep;
    private final TransactionFinalizationStep transactionFinalizationStep;
    private final ValidateSessionStep validateSessionStep;

    @Override
    public FlowExecutor flow(FlowExecutor baseExecutor) {
        return baseExecutor
                .and(getSessionStep)
                .and(validateSessionStep)
                .and(rfcInputStep)
                .and(taxCalculationStep)
                .and(taxAgreementStep)
                .and(passportInformationStep)
                .and(paymentMethodStep)
                .and(paymentValidationStep)
                .and(transactionFinalizationStep)
                .and(completeCheckinStep)
                .andFinally(saveSessionStep);
    }
}
