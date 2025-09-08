package com.aterrizar.service.core.model.session;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record Session(
    UUID sessionId, SessionData sessionData, Status status, UserInformation userInformation)
    implements Serializable {
  public Session withStatus(Status status) {
    return this.toBuilder().status(status).build();
  }

  public Session withSessionData(SessionData sessionData) {
    return this.toBuilder().sessionData(sessionData).build();
  }

  public Session withUserInformation(UserInformation userInformation) {
    return this.toBuilder().userInformation(userInformation).build();
  }
}
