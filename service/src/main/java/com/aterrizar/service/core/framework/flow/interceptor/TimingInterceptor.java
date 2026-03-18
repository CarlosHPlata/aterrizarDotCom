package com.aterrizar.service.core.framework.flow.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;

@Component
public class TimingInterceptor implements StepInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TimingInterceptor.class);

    private long startTime;

    @Override
    public void before(Context context, String stepName) {
        startTime = System.nanoTime();
    }

    @Override
    public void after(Context context, StepResult result, String stepName) {

        long elapsed = System.nanoTime() - startTime;

        log.info(
                "Step: {} - executed: {} - terminal: {} - time(ns): {}",
                stepName,
                result.isSuccess(),
                result.isTerminal(),
                elapsed);
    }
}
