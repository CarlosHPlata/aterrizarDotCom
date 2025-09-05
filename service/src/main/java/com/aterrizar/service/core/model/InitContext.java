package com.aterrizar.service.core.model;

import com.aterrizar.service.core.model.initData.SessionRequest;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@SuperBuilder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class InitContext extends Context {
    private final Optional<SessionRequest> sessionRequest;

    public Optional<SessionRequest> sessionRequest() {
        if (this.sessionRequest == null) {
            return Optional.empty();
        }

        else return sessionRequest;
    }
}
