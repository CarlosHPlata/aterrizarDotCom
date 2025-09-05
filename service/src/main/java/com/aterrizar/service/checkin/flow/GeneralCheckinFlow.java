package com.aterrizar.service.checkin.flow;

import com.aterrizar.service.checkin.steps.*;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeneralCheckinFlow implements FlowStrategy {
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
                .and(agreementSignStep)
                .and(completeCheckinStep)
                .andFinally(saveSessionStep);
    }
}
