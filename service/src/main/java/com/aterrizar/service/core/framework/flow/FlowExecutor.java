package com.aterrizar.service.core.framework.flow;

import java.util.ArrayList;
import java.util.List;

import com.aterrizar.service.core.framework.flow.interceptor.StepInterceptor;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.ExperimentalStepKey;
import com.aterrizar.service.core.model.session.Status;

/**
 * The `FlowExecutor` class is part of the **Chain of Responsibility** design pattern.
 *
 * <p>This class manages a sequence of `Step` objects, allowing a request (represented by a
 * `Context`) to pass through a chain of handlers. Each `Step` processes the request and determines
 * whether the flow should continue or terminate.
 */
public class FlowExecutor {

    /** A list of `Step` objects that define the chain of responsibility. */
    protected List<Step> steps;

    protected Step lastStep;

    /** A list of interceptors that are notified before and after each step execution. */
    protected List<StepInterceptor> interceptors;

    /** Constructs a new `FlowExecutor` with an empty list of steps. */
    public FlowExecutor() {
        this(new ArrayList<>());
    }

    /**
     * Constructs a new `FlowExecutor` with a predefined list of interceptors.
     *
     * @param interceptors the interceptors that will observe the execution of each step
     */
    public FlowExecutor(List<StepInterceptor> interceptors) {
        this.steps = new ArrayList<>();
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();
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
     * Adds an experimental `Step` to the chain of responsibility.
     *
     * <p>This method wraps the provided `Step` in an `ExperimentalStepDecorator`, allowing for
     * experimental features to be toggled based on the provided `ExperimentalStepKey`.
     *
     * @param step the `Step` to be added to the chain
     * @param experimentalStepKey the key used to identify the experimental feature
     * @return the current `FlowExecutor` instance for method chaining
     */
    public FlowExecutor andExperimental(Step step, ExperimentalStepKey experimentalStepKey) {
        this.steps.add(ExperimentalStepDecorator.of(step, experimentalStepKey));
        return this;
    }

    /**
     * Adds a final `Step` to the chain of responsibility.
     *
     * <p>This method sets the last `Step` to be executed after all other steps in the chain. The
     * final step is executed regardless of the results of the previous steps.
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
     *
     * <p>Each `Step` in the chain processes the `Context`. Interceptors are notified before and
     * after each step execution. The flow terminates early if a `Step` returns a terminal result or
     * if specific conditions are met (e.g., failure with a message).
     *
     * @param context the initial `Context` to be processed
     * @return the updated `Context` after processing all applicable steps
     */
    public Context execute(Context context) {

        var updatedContext = context;

        for (Step step : this.steps) {

            String stepName = step.getClass().getSimpleName();

            notifyBefore(updatedContext, stepName);

            var stepResult = step.execute(updatedContext);
            updatedContext = stepResult.context();

            notifyAfter(updatedContext, stepResult, stepName);

            if (!stepResult.isSuccess()
                    && stepResult.isTerminal()
                    && stepResult.message() != null) {

                updatedContext =
                        updatedContext
                                .withStatus(Status.REJECTED)
                                .withCheckinResponse(
                                        responseBuilder ->
                                                responseBuilder.errorMessage(stepResult.message()));

                break;
            }

            if (stepResult.isTerminal()) {
                break;
            }
        }

        if (this.lastStep != null) {

            String stepName = this.lastStep.getClass().getSimpleName();

            notifyBefore(updatedContext, stepName);

            var stepResult = this.lastStep.execute(updatedContext);
            updatedContext = stepResult.context();

            notifyAfter(updatedContext, stepResult, stepName);
        }

        return updatedContext;
    }

    /**
     * Notifies all registered interceptors before a step is executed.
     *
     * @param context the current execution context
     * @param stepName the name of the step that will be executed
     */
    private void notifyBefore(Context context, String stepName) {
        for (StepInterceptor interceptor : interceptors) {
            interceptor.before(context, stepName);
        }
    }

    /**
     * Notifies all registered interceptors after a step has been executed.
     *
     * @param context the current execution context
     * @param result the result produced by the executed step
     * @param stepName the name of the step that was executed
     */
    private void notifyAfter(Context context, StepResult result, String stepName) {
        for (StepInterceptor interceptor : interceptors) {
            interceptor.after(context, result, stepName);
        }
    }
}
