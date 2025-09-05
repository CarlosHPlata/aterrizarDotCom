package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.external.SessionManager;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SaveSessionStep implements Step {
    private final SessionManager sessionManager;

    @Override
    public StepResult onExecute(Context context) {
        sessionManager.saveSession(context.session());

        return StepResult.success(context);
    }
}
