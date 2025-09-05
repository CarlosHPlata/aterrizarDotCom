package com.aterrizar.service.external;

import java.util.UUID;

import com.aterrizar.service.core.model.session.Session;

public interface SessionManager {
  Session getSessionById(UUID sessionId);

  void saveSession(Session session);
}
