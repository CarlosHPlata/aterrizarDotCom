package com.aterrizar.service.checkin.steps;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.PaymentFeature;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.PaymentRequestDto;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.PaymentGateway;

@Component
public class PaymentValidationStep implements Step {

    private final PaymentGateway paymentGateway;
    private final PaymentFeature paymentFeature;

    public PaymentValidationStep(PaymentGateway paymentGateway, PaymentFeature paymentFeature) {
        this.paymentGateway = paymentGateway;
        this.paymentFeature = paymentFeature;
    }

    @Override
    public boolean when(Context context) {
        Map<RequiredField, String> fields = context.checkinRequest().providedFields();
        return fields != null
                && fields.containsKey(RequiredField.PAYMENT_METHOD)
                && (context.session().sessionData() == null
                        || context.session().sessionData().paymentToken() == null);
    }

    @Override
    public StepResult onExecute(Context context) {
        String countryCode = context.countryCode().name();
        List<String> allowedMethods = paymentFeature.getAllowedMethods(countryCode);

        Map<RequiredField, String> fields = context.checkinRequest().providedFields();
        PaymentRequestDto requestDto = PaymentRequestDto.fromProvidedFields(fields);

        if (!allowedMethods.contains(requestDto.paymentMethod())) {
            return StepResult.failure(context, "Payment method blocked for region");
        }
        Optional<RequiredField> missingField = paymentGateway.validateRequiredFields(requestDto);
        if (missingField.isPresent()) {
            return StepResult.terminal(context.withRequiredField(missingField.get()));
        }

        String paymentToken = paymentGateway.executePayment(requestDto);
        return StepResult.success(context.withSessionData(data -> data.paymentToken(paymentToken)));
    }
}
