package com.aterrizar.service.checkin.steps;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.core.model.session.Status;
import com.aterrizar.service.core.model.session.UserInformation;

@Service
public class CreateBaseSessionStep implements Step {
  @Override
  public boolean when(Context context) {
    if (context instanceof InitContext initContext) {
      return initContext.sessionRequest().isPresent();
    }
    return false;
  }

  @Override
  public StepResult onExecute(Context context) {
    var initContext = (InitContext) context;
    var sessionRequest = initContext.sessionRequest().orElseThrow();

    var sessionId = UUID.randomUUID();
    var session =
        Session.builder()
            .sessionId(sessionId)
            .userInformation(UserInformation.builder().userId(sessionRequest.userId()).build())
            .status(Status.INITIALIZED)
            .build();

    return StepResult.success(context.withSession(session));
  }
}
