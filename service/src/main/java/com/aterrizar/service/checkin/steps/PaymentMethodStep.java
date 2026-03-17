package com.aterrizar.service.checkin.steps;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.PaymentFeature;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;

@Component
public class PaymentMethodStep implements Step {

    private final PaymentFeature paymentFeature;

    public PaymentMethodStep(PaymentFeature paymentFeature) {
        this.paymentFeature = paymentFeature;
    }

    @Override
    public boolean when(Context context) {
        String countryCode = context.countryCode().name();
        return !paymentFeature.getAllowedMethods(countryCode).isEmpty();
    }

    @Override
    public StepResult onExecute(Context context) {
        String countryCode = context.countryCode().name();

        List<String> allowedMethods = paymentFeature.getAllowedMethods(countryCode);

        Map<RequiredField, String> providedFields = context.checkinRequest().providedFields();
        String paymentMethod =
                providedFields != null ? providedFields.get(RequiredField.PAYMENT_METHOD) : null;

        if (paymentMethod == null || paymentMethod.isBlank()) {
            return StepResult.terminal(context.withRequiredField(RequiredField.PAYMENT_METHOD));
        }

        if (!allowedMethods.contains(paymentMethod)) {
            return StepResult.failure(
                    context,
                    "Payment method "
                            + paymentMethod
                            + " is not allowed for country "
                            + countryCode);
        }

        return StepResult.success(context);
    }
}
