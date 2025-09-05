package com.aterrizar.service.core.framework.strategy;

import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.neovisionaries.i18n.CountryCode;

/**
 * Abstract class representing a strategy for country-specific check-in processes.
 * <p>
 * This class is part of the Strategy Design Pattern, where concrete implementations
 * define specific behaviors for different countries.
 */
public abstract class CheckinCountryStrategy {

    /**
     * Initializes the check-in process using the provided context.
     *
     * @param initialContext the initial context for the check-in process
     * @return the resulting {@link InitContext} after initialization
     */
    public InitContext init(Context initialContext) {
        var baseExecutor = getInitBaseExecutor();
        return (InitContext) this.getInitFlow()
                .flow(baseExecutor)
                .execute(initialContext);
    }

    /**
     * Executes the check-in process using the provided initial context.
     *
     * @param initialContext the initial {@link Context} for the check-in process
     * @return the resulting {@link Context} after executing the check-in flow
     */
    public Context checkin(Context initialContext) {
        var baseExecutor = getCheckinBaseExecutor();
        return this.getCheckinFlow()
                .flow(baseExecutor)
                .execute(initialContext);
    }

    /**
     * Provides the flow strategy for the initialization process.
     *
     * @return the {@link FlowStrategy} for initialization
     */
    protected abstract FlowStrategy getInitFlow();

    /**
     * Provides the flow strategy for the check-in process.
     *
     * @return the {@link FlowStrategy} for check-in
     */
    protected abstract FlowStrategy getCheckinFlow();

    /**
     * Provides the base executor for the initialization process.
     *
     * @return the {@link FlowExecutor} for initialization
     */
    protected FlowExecutor getInitBaseExecutor() {
        return new FlowExecutor();
    }

    /**
     * Provides the base executor for the check-in process.
     *
     * @return the {@link FlowExecutor} for check-in
     */
    protected FlowExecutor getCheckinBaseExecutor() {
        return new FlowExecutor();
    }

    /**
     * Retrieves the country code associated with this strategy.
     *
     * @return the {@link CountryCode} for the strategy
     */
    public abstract CountryCode getCountryCode();
}
