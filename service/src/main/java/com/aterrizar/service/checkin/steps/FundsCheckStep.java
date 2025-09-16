package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;

@Service
public class FundsCheckStep implements Step {

    @Override
    public boolean when(Context context) {
        return false;
    }

    @Override
    public StepResult onExecute(Context context) {
        return StepResult.success(context);
    }
}
