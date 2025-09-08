package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.external.SessionManager;

@ExtendWith(MockitoExtension.class)
class SaveSessionStepTest {
    @Mock private SessionManager sessionManager;

    @InjectMocks private SaveSessionStep saveSessionStep;

    @Test
    void onExecute_shouldSaveSessionAndReturnSuccess() {
        var session = Session.builder().sessionId(UUID.randomUUID()).build();
        Context mockContext = mock(Context.class);
        when(mockContext.session()).thenReturn(session);

        StepResult result = saveSessionStep.onExecute(mockContext);

        verify(sessionManager).saveSession(session);
        assertTrue(result.isSuccess());
    }
}
