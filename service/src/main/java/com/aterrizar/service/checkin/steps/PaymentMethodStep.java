package com.aterrizar.service.checkin.steps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;

@Component
public class PaymentMethodStep implements Step {

    private final Environment environment;

    public PaymentMethodStep(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean when(Context context) {
        String countryCode = context.countryCode().name();
        return environment.containsProperty("feature.tax.payments." + countryCode);
    }

    @Override
    public StepResult onExecute(Context context) {
        String countryCode = context.countryCode().name();
        String allowedMethodsProperty =
                environment.getProperty("feature.tax.payments." + countryCode);
        List<String> allowedMethods = Arrays.asList(allowedMethodsProperty.split(","));

        Map<RequiredField, String> providedFields = context.checkinRequest().providedFields();
        String paymentMethod =
                providedFields != null ? providedFields.get(RequiredField.PAYMENT_METHOD) : null;

        // CORRECCIÓN: Usar StepResult.terminal() para interrumpir la cadena y pedir el dato
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
