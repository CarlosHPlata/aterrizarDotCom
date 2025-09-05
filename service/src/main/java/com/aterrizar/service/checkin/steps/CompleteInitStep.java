package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Status;

@Service
public class CompleteInitStep implements Step {
  @Override
  public StepResult onExecute(Context context) {
    var updatedContext = context.withStatus(Status.INITIALIZED);
    return StepResult.terminal(updatedContext);
  }
}
