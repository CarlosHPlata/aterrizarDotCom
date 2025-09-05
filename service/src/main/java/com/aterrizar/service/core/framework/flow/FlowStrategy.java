package com.aterrizar.service.core.framework.flow;

/**
 * The `FlowStrategy` interface defines a contract for creating a flow process.
 * <p>
 * <b>Design Pattern:</b> Strategy Pattern
 * <p>
 * This interface is part of the Strategy design pattern, allowing different flow strategies
 * to be implemented and used interchangeably at runtime.
 */
public interface FlowStrategy {
    /**
     * Configures and returns a `FlowExecutor` by applying the specific flow strategy.
     *
     * @param baseExecutor the base `FlowExecutor` to which the strategy is applied
     * @return the configured `FlowExecutor`
     */
    FlowExecutor flow(FlowExecutor baseExecutor);
}
