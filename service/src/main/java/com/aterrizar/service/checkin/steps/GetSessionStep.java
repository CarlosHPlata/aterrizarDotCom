package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.SessionManager;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetSessionStep implements Step {
    private final SessionManager sessionManager;

    @Override
    public StepResult onExecute(Context context) {
        try {
            var session = sessionManager.getSessionById(context.checkinRequest().sessionId());
            if (session == null) {
                return StepResult.failure(context, "Session not found");
            }

            return StepResult.success(context.withSession(session));

        } catch (IllegalArgumentException e) {
            return StepResult.failure(context, "Invalid session ID");
        }
    }
}
