package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.framework.flow.interceptor.TimingInterceptor;
import com.aterrizar.service.core.model.Context;

@ExtendWith(MockitoExtension.class)
class TimingInterceptorTest {

    private TimingInterceptor interceptor;

    @Mock private Context context;

    @Mock private StepResult stepResult;

    @BeforeEach
    void setUp() {
        interceptor = new TimingInterceptor();
    }

    @Test
    void shouldExecuteBeforeAndAfterWithoutErrors() {

        when(stepResult.isSuccess()).thenReturn(true);
        when(stepResult.isTerminal()).thenReturn(false);

        interceptor.before(context, "TestStep");

        interceptor.after(context, stepResult, "TestStep");

        // Si llega aquí sin excepción, el interceptor funciona
        assertTrue(true);
    }
}
