package com.aterrizar.service.checkin.flow;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.steps.CompleteInitStep;
import com.aterrizar.service.checkin.steps.CreateBaseSessionDataStep;
import com.aterrizar.service.checkin.steps.CreateBaseSessionStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.core.framework.flow.FlowExecutor;
import com.aterrizar.service.core.framework.flow.FlowStrategy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GeneralInitFlow implements FlowStrategy {

  private final CreateBaseSessionStep createBaseSessionStep;
  private final CreateBaseSessionDataStep createBaseSessionDataStep;
  private final SaveSessionStep saveSessionStep;
  private final CompleteInitStep completeInitStep;

  @Override
  public FlowExecutor flow(FlowExecutor baseExecutor) {
    return baseExecutor
        .and(createBaseSessionStep)
        .and(createBaseSessionDataStep)
        .and(saveSessionStep)
        .and(completeInitStep);
  }
}
