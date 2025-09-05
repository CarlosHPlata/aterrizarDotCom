package com.aterrizar.service.core.framework.flow;

import com.aterrizar.service.core.model.Context;

import jakarta.annotation.Nullable;

public record StepResult(
    boolean isSuccess, boolean isTerminal, Context context, @Nullable String message) {
  public static StepResult success(Context context) {
    return new StepResult(true, false, context, null);
  }

  public static StepResult failure(Context context, String message) {
    return new StepResult(false, true, context, message);
  }

  public static StepResult terminal(Context context) {
    return new StepResult(true, true, context, null);
  }
}
