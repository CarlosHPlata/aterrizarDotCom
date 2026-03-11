package com.aterrizar.service.core.framework.flow;

import com.aterrizar.service.core.model.Context;

public abstract class CompositeStep implements Step {
    protected abstract FlowExecutor registerSteps(FlowExecutor executor);

    @Override
    public StepResult onExecute(Context context) {
        var executor = registerSteps(new FlowExecutor());
        return executor.executeWithResult(context);
    }
}
