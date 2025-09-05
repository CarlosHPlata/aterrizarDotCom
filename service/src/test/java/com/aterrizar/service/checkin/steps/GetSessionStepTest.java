package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.external.SessionManager;

@ExtendWith(MockitoExtension.class)
class GetSessionStepTest {
  @Mock private SessionManager sessionManager;

  @InjectMocks private GetSessionStep getSessionStep;

  @Test
  void onExecuteShouldGetTheSession() {
    var context = mock(Context.class);
    var sessionId = UUID.randomUUID();
    var checkinRequest = CheckinRequest.builder().sessionId(sessionId).build();
    var session = Session.builder().sessionId(sessionId).build();

    when(context.checkinRequest()).thenReturn(checkinRequest);
    when(sessionManager.getSessionById(sessionId)).thenReturn(session);
    when(context.withSession(session)).thenReturn(context);

    var result = getSessionStep.onExecute(context);

    assertTrue(result.isSuccess());
    assertEquals(context, result.context());
    verify(sessionManager).getSessionById(sessionId);
  }

  @Test
  void onExecuteShouldEndWithFailureIfSessionIdIsNull() {
    var context = mock(Context.class);
    var checkinRequest = CheckinRequest.builder().sessionId(null).build();

    when(context.checkinRequest()).thenReturn(checkinRequest);
    when(sessionManager.getSessionById(null)).thenThrow(new IllegalArgumentException());

    var result = getSessionStep.onExecute(context);

    assertFalse(result.isSuccess());
    assertEquals("Invalid session ID", result.message());
  }

  @Test
  void onExecuteShouldEndWithFailureIfSessionDoesNotExists() {
    var context = mock(Context.class);
    var sessionId = UUID.randomUUID();
    var checkinRequest = CheckinRequest.builder().sessionId(sessionId).build();

    when(context.checkinRequest()).thenReturn(checkinRequest);
    when(sessionManager.getSessionById(sessionId)).thenThrow(new IllegalArgumentException());

    var result = getSessionStep.onExecute(context);

    assertFalse(result.isSuccess());
    assertEquals("Invalid session ID", result.message());
  }
}
