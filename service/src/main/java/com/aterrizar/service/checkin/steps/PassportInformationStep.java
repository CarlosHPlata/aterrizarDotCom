package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;

@Service
public class PassportInformationStep implements Step {

  @Override
  public StepResult onExecute(Context context) {
    var optionalRequest = Optional.ofNullable(context.checkinRequest());

    if (isFieldFilled(optionalRequest)) {
      var updatedContext = capturePassportNumber(context);
      return StepResult.success(updatedContext);
    }

    var updatedContext = requestPassportNumber(context);
    return StepResult.terminal(updatedContext);
  }

  @Override
  public boolean when(Context context) {
    var session = context.session();
    var userInfo = session.userInformation();

    return Optional.ofNullable(userInfo).isPresent()
        && Optional.ofNullable(userInfo.passportNumber()).isEmpty();
  }

  private Context capturePassportNumber(Context context) {
    var optionalRequest = Optional.ofNullable(context.checkinRequest());

    return optionalRequest
        .map(CheckinRequest::providedFields)
        .map(fields -> fields.get(RequiredField.PASSPORT_NUMBER))
        .map(
            passportNumber ->
                context.withUserInformation(builder -> builder.passportNumber(passportNumber)))
        .orElseThrow(() -> new IllegalStateException("Passport number is missing in the request."));
  }

  private static boolean isFieldFilled(Optional<CheckinRequest> optionalRequest) {
    return optionalRequest.isPresent()
        && optionalRequest.get().providedFields().get(RequiredField.PASSPORT_NUMBER) != null;
  }

  private Context requestPassportNumber(Context context) {
    return context.withRequiredField(RequiredField.PASSPORT_NUMBER);
  }
}
