package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.ExperimentalStepKey;

import lombok.Getter;

public class ExperimentalStepDecorator implements Step {

    @Getter private final Step delegate;
    private final ExperimentalStepKey experimentKey;

    private ExperimentalStepDecorator(Step delegate, ExperimentalStepKey experimentKey) {
        this.delegate = delegate;
        this.experimentKey = experimentKey;
    }

    public static ExperimentalStepDecorator of(Step delegate, ExperimentalStepKey experimentKey) {
        return new ExperimentalStepDecorator(delegate, experimentKey);
    }

    @Override
    public boolean when(Context context) {
        return isAvailableExperimentally(context) && delegate.when(context);
    }

    @Override
    public StepResult onExecute(Context context) {
        return delegate.onExecute(context);
    }

    private boolean isAvailableExperimentally(Context context) {
        return context.session().experimentalData() != null
                && context.session().experimentalData().isExperimentActive(experimentKey);
    }
}
