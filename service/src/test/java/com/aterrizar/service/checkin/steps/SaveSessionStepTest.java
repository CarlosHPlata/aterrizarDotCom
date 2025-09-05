package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.external.SessionManager;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveSessionStepTest {
    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private SaveSessionStep saveSessionStep;

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