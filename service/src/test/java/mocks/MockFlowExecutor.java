package mocks;

import java.util.ArrayList;
import java.util.List;

import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.model.Context;

public class MockFlowExecutor extends FlowExecutor {
  private final List<String> executedSteps = new ArrayList<>();

  @Override
  public Context execute(Context context) {
    for (Step step : this.steps) {
      executedSteps.add(step.getClass().getSimpleName());
    }

    return context;
  }

  public List<String> getExecutedSteps() {
    return executedSteps;
  }
}
