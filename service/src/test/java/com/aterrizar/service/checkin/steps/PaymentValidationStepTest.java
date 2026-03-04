package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.payment.facade.PaymentFacade;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class PaymentValidationStepTest {

    private PaymentValidationStep paymentValidationStep;
    private PaymentFacade paymentFacade;
    private Environment environment;

    @BeforeEach
    void setUp() {
        paymentFacade = mock(PaymentFacade.class);
        environment = mock(Environment.class);
        paymentValidationStep = new PaymentValidationStep(paymentFacade, environment);
    }

    @Test
    void shouldRequestCardNumberWhen3dsIsSelectedButEmpty() {
        when(environment.getProperty(anyString(), anyString())).thenReturn("3DS,WIRE,GOV");
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.PAYMENT_METHOD, "3DS"
                                                        // It's missing the CARD_NUMBER, which is
                                                        // required for 3DS
                                                        )));

        var result = paymentValidationStep.onExecute(context);

        assertTrue(result.isTerminal());
        assertTrue(
                result.context()
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.CARD_NUMBER));
    }

    @Test
    void shouldGenerateTokenAndSaveInSessionWhenDataIsComplete() {
        when(environment.getProperty(anyString(), anyString())).thenReturn("3DS,WIRE,GOV");
        when(paymentFacade.executePayment(any())).thenReturn("MOCK-TOKEN-123");

        var context =
                MockContext.initializedMock(CountryCode.CA)
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.PAYMENT_METHOD, "GOV",
                                                        RequiredField.CURP_NUMBER, "CURP12345")));

        var result = paymentValidationStep.onExecute(context);

        assertTrue(result.isSuccess());
        assertEquals("MOCK-TOKEN-123", result.context().session().sessionData().paymentToken());
    }
}
