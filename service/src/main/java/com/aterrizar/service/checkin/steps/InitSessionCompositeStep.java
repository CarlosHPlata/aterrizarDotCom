package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.CompositeStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InitSessionCompositeStep extends CompositeStep {

    private final GetSessionStep getSessionStep;
    private final ValidateSessionStep validateSessionStep;

    @Override
    protected FlowExecutor registerSteps(FlowExecutor executor) {
        return executor.and(getSessionStep).and(validateSessionStep);
    }
}
