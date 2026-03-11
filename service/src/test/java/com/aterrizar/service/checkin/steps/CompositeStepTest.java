package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.framework.flow.CompositeStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.session.Status;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class CompositeStepTest {

    @Test
    void shouldExecuteSubStepsAndReturnSuccess() {
        var context = MockContext.initializedMock(CountryCode.US);
        var step1 = mock(Step.class);
        var step2 = mock(Step.class);
        when(step1.when(any())).thenReturn(true);
        when(step2.when(any())).thenReturn(true);
        when(step1.execute(any())).thenReturn(StepResult.success(context));
        when(step2.execute(any())).thenReturn(StepResult.success(context));

        var composite =
                new CompositeStep() {
                    @Override
                    protected FlowExecutor registerSteps(FlowExecutor executor) {
                        return executor.and(step1).and(step2);
                    }
                };

        var result = composite.onExecute(context);

        assertTrue(result.isSuccess());
        assertFalse(result.isTerminal());
    }

    @Test
    void shouldReturnTerminalWhenSubStepFails() {
        var context = MockContext.initializedMock(CountryCode.US);
        var failingStep = mock(Step.class);
        when(failingStep.when(any())).thenReturn(true);
        when(failingStep.execute(any()))
                .thenReturn(StepResult.failure(context, "Session not found"));

        var composite =
                new CompositeStep() {
                    @Override
                    protected FlowExecutor registerSteps(FlowExecutor executor) {
                        return executor.and(failingStep);
                    }
                };

        var result = composite.onExecute(context);

        assertTrue(result.isTerminal());
        assertEquals(Status.REJECTED, result.context().status());
        assertNull(result.message());
    }

    @Test
    void shouldNotExecuteNextSubStepAfterFailure() {
        var context = MockContext.initializedMock(CountryCode.US);
        var failingStep = mock(Step.class);
        var nextStep = mock(Step.class);
        when(failingStep.when(any())).thenReturn(true);
        when(failingStep.execute(any())).thenReturn(StepResult.failure(context, "error"));

        var composite =
                new CompositeStep() {
                    @Override
                    protected FlowExecutor registerSteps(FlowExecutor executor) {
                        return executor.and(failingStep).and(nextStep);
                    }
                };

        composite.onExecute(context);

        verify(nextStep, never()).execute(any());
    }
}
