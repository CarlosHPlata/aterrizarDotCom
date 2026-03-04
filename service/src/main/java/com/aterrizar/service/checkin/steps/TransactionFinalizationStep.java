package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Component;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.payment.port.ExternalPaymentPort;

@Component
public class TransactionFinalizationStep implements Step {

    private final ExternalPaymentPort externalPaymentPort;

    public TransactionFinalizationStep(ExternalPaymentPort externalPaymentPort) {
        this.externalPaymentPort = externalPaymentPort;
    }

    @Override
    public boolean when(Context context) {
        return context.session().sessionData() != null
                && context.session().sessionData().paymentToken() != null;
    }

    @Override
    public StepResult onExecute(Context context) {
        String token = context.session().sessionData().paymentToken();

        // Usamos el Port para cumplir con el Mock Behavior: GET /payment-service/v1/status/{token}
        String status = externalPaymentPort.getPaymentStatus(token);
        boolean isSuccess = "SUCCESS".equalsIgnoreCase(status);

        if (!isSuccess) {
            // Retornamos terminal para pausar el flujo y permitir el Polling
            return StepResult.terminal(context);
        }

        return StepResult.success(context);
    }
}
