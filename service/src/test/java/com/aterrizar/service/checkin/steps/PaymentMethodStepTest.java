package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;
import mocks.MockContext;

class PaymentMethodStepTest {

    private PaymentMethodStep paymentMethodStep;
    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        paymentMethodStep = new PaymentMethodStep(environment);
    }

    @Test
    void shouldExecuteWhenPropertyExistsForCountry() {
        when(environment.containsProperty("feature.tax.payments.US")).thenReturn(true);
        var context = MockContext.initializedMock(CountryCode.US);
        
        assertTrue(paymentMethodStep.when(context));
    }

    @Test
    void shouldRequestPaymentMethodWhenMissing() {
        when(environment.getProperty("feature.tax.payments.MX")).thenReturn("3DS,WIRE");
        var context = MockContext.initializedMock(CountryCode.MX); 

        var result = paymentMethodStep.onExecute(context);

        assertTrue(result.isTerminal());
        assertTrue(result.context().checkinResponse().providedFields().contains(RequiredField.PAYMENT_METHOD));
    }

    @Test
    void shouldReturnFailureWhenMethodIsNotAllowed() {
        when(environment.getProperty("feature.tax.payments.MX")).thenReturn("3DS,WIRE");
        var context = MockContext.initializedMock(CountryCode.MX)
                .withCheckinRequest(builder -> builder.providedFields(Map.of(
                        RequiredField.PAYMENT_METHOD, "GOV"
                )));

        var result = paymentMethodStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.message().contains("is not allowed for country MX"));
    }
}