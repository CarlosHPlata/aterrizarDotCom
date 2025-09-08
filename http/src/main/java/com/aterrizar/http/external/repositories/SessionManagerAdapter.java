package com.aterrizar.http.external.repositories;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aterrizar.http.external.repositories.redis.session.model.RedisSession;
import com.aterrizar.http.external.repositories.redis.session.repository.CheckinSessionRepository;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.external.SessionManager;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SessionManagerAdapter implements SessionManager {
  private final CheckinSessionRepository checkinRepository;

  @Override
  public Session getSessionById(UUID sessionId) {
    return checkinRepository
        .findById(sessionId.toString())
        .map(RedisSession::getSession)
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));
  }

  @Override
  public void saveSession(Session session) {
    var redisSession = new RedisSession(session.sessionId().toString(), session);
    checkinRepository.save(redisSession);
  }
}
