package com.aterrizar.service.checkin.flow;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.steps.AgreementSignStep;
import com.aterrizar.service.checkin.steps.CompleteCheckinStep;
import com.aterrizar.service.checkin.steps.FundsCheckStep;
import com.aterrizar.service.checkin.steps.InitSessionCompositeStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VeContinueFlow implements FlowStrategy {
    private final PassportInformationStep passportInformationStep;
    private final AgreementSignStep agreementSignStep;
    private final SaveSessionStep saveSessionStep;
    private final CompleteCheckinStep completeCheckinStep;
    private final FundsCheckStep fundsCheckStep;
    private final InitSessionCompositeStep initSessionCompositeStep;

    @Override
    public FlowExecutor flow(FlowExecutor baseExecutor) {
        return baseExecutor
                .and(initSessionCompositeStep)
                .and(fundsCheckStep)
                .and(passportInformationStep)
                .and(agreementSignStep)
                .and(completeCheckinStep)
                .andFinally(saveSessionStep);
    }
}
