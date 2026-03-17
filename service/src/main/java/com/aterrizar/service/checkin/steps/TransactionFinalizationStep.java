package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Component;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.PaymentGateway;

@Component
public class TransactionFinalizationStep implements Step {

    private final PaymentGateway externalPaymentPort;

    public TransactionFinalizationStep(PaymentGateway externalPaymentPort) {
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

        String status = externalPaymentPort.getPaymentStatus(token);
        boolean isSuccess = PaymentGateway.PAYMENT_SUCCESS_STATUS.equalsIgnoreCase(status);

        if (!isSuccess) {
            return StepResult.terminal(context);
        }

        return StepResult.success(context);
    }
}
