package com.aterrizar.service.checkin.flow;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.steps.AgreementSignStep;
import com.aterrizar.service.checkin.steps.CompleteCheckinStep;
import com.aterrizar.service.checkin.steps.ExperimentalStepDecorator;
import com.aterrizar.service.checkin.steps.GetSessionStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.checkin.steps.ValidateSessionStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import com.aterrizar.service.core.model.ExperimentalStepKey;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GeneralContinueFlow implements FlowStrategy {
    private final GetSessionStep getSessionStep;
    private final ValidateSessionStep validateSessionStep;
    private final PassportInformationStep passportInformationStep;
    private final AgreementSignStep agreementSignStep;
    private final SaveSessionStep saveSessionStep;
    private final CompleteCheckinStep completeCheckinStep;

    @Override
    public FlowExecutor flow(FlowExecutor baseExecutor) {
        return baseExecutor
                .and(getSessionStep)
                .and(validateSessionStep)
                .and(passportInformationStep)
                .and(
                        ExperimentalStepDecorator.of(
                                agreementSignStep, ExperimentalStepKey.AGREEMENT_SIGN))
                .and(completeCheckinStep)
                .andFinally(saveSessionStep);
    }
}
