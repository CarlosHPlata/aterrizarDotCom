package com.aterrizar.service.core.framework.flow.interceptor;

import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;

public interface StepInterceptor {

    void before(Context context, String stepName);

    void after(Context context, StepResult result, String stepName);
}
