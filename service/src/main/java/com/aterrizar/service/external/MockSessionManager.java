package com.aterrizar.service.external;

import com.aterrizar.service.core.model.session.Session;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MockSessionManager implements SessionManager {
    private static final Map<UUID, Session> sessionStore = new HashMap<>();

    @Override
    public Session getSessionById(java.util.UUID sessionId) {
        return sessionStore.get(sessionId);
    }

    @Override
    public void saveSession(Session session) {
        sessionStore.put(session.sessionId(), session);
    }

}
