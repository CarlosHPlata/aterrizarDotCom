package com.aterrizar.service.core.framework.flow;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * The `FlowExecutor` class is part of the **Chain of Responsibility** design pattern.
 * <p>
 * This class manages a sequence of `Step` objects, allowing a request (represented by a `Context`)
 * to pass through a chain of handlers. Each `Step` processes the request and determines whether
 * the flow should continue or terminate.
 * </p>
 */
public class FlowExecutor {
    /**
     * A list of `Step` objects that define the chain of responsibility.
     */
    protected List<Step> steps;

    protected Step lastStep;

    /**
     * Constructs a new `FlowExecutor` with an empty list of steps.
     */
    public FlowExecutor() {
        this.steps = new ArrayList<>();
    }

    /**
     * Adds a `Step` to the chain of responsibility.
     *
     * @param step the `Step` to be added to the chain
     * @return the current `FlowExecutor` instance for method chaining
     */
    public FlowExecutor and(Step step) {
        this.steps.add(step);
        return this;
    }

    /**
     * Adds a final `Step` to the chain of responsibility.
     * <p>
     * This method sets the last `Step` to be executed after all other steps in the chain.
     * The final step is executed regardless of the results of the previous steps.
     * </p>
     *
     * @param step the `Step` to be added as the final step
     * @return the current `FlowExecutor` instance for method chaining
     */
    public FlowExecutor andFinally(Step step) {
        this.lastStep = step;
        return this;
    }

    /**
     * Executes the chain of responsibility using the provided `Context`.
     * <p>
     * Each `Step` in the chain processes the `Context`. The flow terminates early if a `Step`
     * returns a terminal result or if specific conditions are met (e.g., failure with a message).
     * </p>
     *
     * @param context the initial `Context` to be processed
     * @return the updated `Context` after processing all applicable steps
     */
    public Context execute(Context context) {
        var updatedContext = context;
        for (Step step : this.steps) {
            var stepResult = step.execute(updatedContext);
            updatedContext = stepResult.context();

            if(!stepResult.isSuccess() && stepResult.isTerminal() && stepResult.message() != null) {
                updatedContext = updatedContext
                        .withStatus(Status.REJECTED)
                        .withCheckinResponse(responseBuilder -> responseBuilder
                                .errorMessage(stepResult.message()));
                break;
            }

            if (stepResult.isTerminal()) {
                break;
            }
        }

        if (this.lastStep != null) {
            var stepResult = this.lastStep.execute(updatedContext);
            updatedContext = stepResult.context();
        }
        return updatedContext;
    }
}
