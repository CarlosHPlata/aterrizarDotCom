package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.model.ExperimentalStepKey;
import com.aterrizar.service.core.model.session.ExperimentalData;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class ExperimentalStepDecoratorTest {
    private Step delegate;
    private ExperimentalStepKey experimentKey;

    @BeforeEach
    void setUp() {
        delegate = mock(Step.class);
        experimentKey = ExperimentalStepKey.AGREEMENT_SIGN;
    }

    @Test
    void shouldNotRunWhenExperimentNotActive() {
        var context = MockContext.initializedMock(CountryCode.US);
        var decorator = ExperimentalStepDecorator.of(delegate, experimentKey);
        assertFalse(decorator.when(context));
        verify(delegate, never()).when(any());
        verify(delegate, never()).onExecute(any());
    }

    @Test
    void shouldRunWhenExperimentActiveAndDelegateWhenTrue() {
        var expData =
                ExperimentalData.builder().experiments(List.of(experimentKey.value())).build();
        var context = MockContext.initializedMock(CountryCode.US).withExperimentalData(expData);
        when(delegate.when(any())).thenReturn(true);

        var decorator = ExperimentalStepDecorator.of(delegate, experimentKey);
        assertTrue(decorator.when(context));
        verify(delegate).when(context);
        verify(delegate, never()).onExecute(any());
    }

    @Test
    void shouldNotRunWhenExperimentActiveButDelegateWhenFalse() {

        var expData =
                ExperimentalData.builder().experiments(List.of(experimentKey.value())).build();
        var context = MockContext.initializedMock(CountryCode.US).withExperimentalData(expData);

        when(delegate.when(context)).thenReturn(false);
        var decorator = ExperimentalStepDecorator.of(delegate, experimentKey);

        assertFalse(decorator.when(context));
        verify(delegate).when(context);
        verify(delegate, never()).onExecute(any());
    }
}
