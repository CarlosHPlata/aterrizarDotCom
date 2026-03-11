package com.aterrizar.service.checkin.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.framework.flow.StepResult;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

@ExtendWith(MockitoExtension.class)
class InitSessionCompositeStepTest {

    @Mock private GetSessionStep getSessionStep;
    @Mock private ValidateSessionStep validateSessionStep;
    @InjectMocks private InitSessionCompositeStep initSessionCompositeStep;

    @Test
    void shouldExecuteGetSessionAndValidateSessionStepsInOrder() {
        var context = MockContext.initializedMock(CountryCode.US);

        when(getSessionStep.execute(any())).thenReturn(StepResult.success(context));
        when(validateSessionStep.execute(any())).thenReturn(StepResult.success(context));

        initSessionCompositeStep.onExecute(context);

        var order = inOrder(getSessionStep, validateSessionStep);
        order.verify(getSessionStep).execute(any());
        order.verify(validateSessionStep).execute(any());
    }
}
