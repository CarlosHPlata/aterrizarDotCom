package com.aterrizar.service.core.framework.flow;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Status;

public abstract class CompositeStep implements Step {
    protected abstract FlowExecutor registerSteps(FlowExecutor executor);

    @Override
    public StepResult onExecute(Context context) {
        var executor = registerSteps(new FlowExecutor());
        var resultContext = executor.execute(context);

        if (resultContext.session() != null && Status.REJECTED.equals(resultContext.status())) {
            return StepResult.terminal(resultContext);
        }

        return StepResult.success(resultContext);
    }
}
