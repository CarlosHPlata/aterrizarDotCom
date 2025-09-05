package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.SessionManager;

import lombok.AllArgsConstructor;

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
