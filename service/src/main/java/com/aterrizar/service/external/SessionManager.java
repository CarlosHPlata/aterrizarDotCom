package com.aterrizar.service.external;

import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.core.model.session.SessionData;
import com.aterrizar.service.core.model.session.Status;

import java.util.UUID;

public interface SessionManager {
    Session getSessionById(UUID sessionId);
    void saveSession(Session session);
}
