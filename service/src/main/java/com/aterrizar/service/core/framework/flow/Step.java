package com.aterrizar.service.core.framework.flow;

import com.aterrizar.service.core.model.Context;

/**
 * The `Step` interface defines the structure for a step in a flow process (Chain of
 * responsibility).
 *
 * <p>Design Patterns Used:
 *
 * <p>- <b>Template Method</b>: The `execute` method provides a template for the execution process,
 * delegating specific behavior to the `when` and `onExecute` methods.
 */
public interface Step {

    /**
     * Executes the step. If the `when` condition is met, the `onExecute` method is called.
     * Otherwise, it returns a successful result with the current context.
     *
     * @param context the context in which the step is executed
     * @return the result of the step execution
     */
    default StepResult execute(Context context) {
        if (this.when(context)) {
            return this.onExecute(context);
        }

        return StepResult.success(context);
    }

    /**
     * Determines whether the step should be executed. By default, this method always returns
     * `true`.
     *
     * @param context the context in which the condition is evaluated
     * @return `true` if the step should be executed, `false` otherwise
     */
    default boolean when(Context context) {
        return true;
    }

    /**
     * Defines the main behavior of the step when executed. This method must be implemented by all
     * classes that implement the `Step` interface.
     *
     * @param context the context in which the step is executed
     * @return the result of the step execution
     */
    StepResult onExecute(Context context);
}
