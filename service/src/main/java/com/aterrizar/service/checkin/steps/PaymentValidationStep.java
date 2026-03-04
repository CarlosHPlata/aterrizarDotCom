package com.aterrizar.service.checkin.steps;

import java.util.Arrays;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.payment.facade.PaymentFacade;
import com.aterrizar.service.payment.model.PaymentRequestDto;

@Component
public class PaymentValidationStep implements Step {

    private final PaymentFacade paymentFacade;
    private final Environment environment;

    public PaymentValidationStep(PaymentFacade paymentFacade, Environment environment) {
        this.paymentFacade = paymentFacade;
        this.environment = environment;
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
        String allowedMethodsProperty =
                environment.getProperty("feature.tax.payments." + countryCode, "");

        Map<RequiredField, String> fields = context.checkinRequest().providedFields();
        PaymentRequestDto requestDto = PaymentRequestDto.fromProvidedFields(fields);

        if (!Arrays.asList(allowedMethodsProperty.split(","))
                .contains(requestDto.paymentMethod())) {
            return StepResult.failure(context, "Payment method blocked for region");
        }

        RequiredField missingField = checkMissingSpecificField(requestDto);
        if (missingField != null) {
            return StepResult.terminal(context.withRequiredField(missingField));
        }

        String paymentToken = paymentFacade.executePayment(requestDto);
        return StepResult.success(context.withSessionData(data -> data.paymentToken(paymentToken)));
    }

    private RequiredField checkMissingSpecificField(PaymentRequestDto dto) {
        return switch (dto.paymentMethod().toUpperCase()) {
            case "3DS" ->
                    (dto.cardNumber() == null || dto.cardNumber().isBlank())
                            ? RequiredField.CARD_NUMBER
                            : null;
            case "WIRE" ->
                    (dto.linkIdentifier() == null || dto.linkIdentifier().isBlank())
                            ? RequiredField.LINK_IDENTIFIER
                            : null;
            case "GOV" ->
                    (dto.curpNumber() == null || dto.curpNumber().isBlank())
                            ? RequiredField.CURP_NUMBER
                            : null;
            default -> null;
        };
    }
}
