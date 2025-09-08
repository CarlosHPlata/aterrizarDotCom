package com.aterrizar.service.core.model;

import java.util.Optional;

import com.aterrizar.service.core.model.init.SessionRequest;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class InitContext extends Context {
    private final Optional<SessionRequest> sessionRequest;

    public Optional<SessionRequest> sessionRequest() {
        if (this.sessionRequest == null) {
            return Optional.empty();
        } else {
            return sessionRequest;
        }
    }
}
