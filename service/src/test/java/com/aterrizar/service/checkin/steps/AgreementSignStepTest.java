package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class AgreementSignStepTest {
    private AgreementSignStep agreementSignStep;

    @BeforeEach
    void setUp() {
        agreementSignStep = new AgreementSignStep();
    }

    @Test
    void shouldExecuteWhenNoAgreementIsSignedYet() {
        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withSessionData(builder -> builder.agreementSigned(false));
        var result = agreementSignStep.when(context);

        assertTrue(result);
    }

    @Test
    void shouldExecuteWhenAgreementIsNotSet() {
        var context = MockContext.initializedMock(CountryCode.AD);
        var result = agreementSignStep.when(context);

        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenAgreementIsAlreadySigned() {
        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withSessionData(builder -> builder.agreementSigned(true));
        var result = agreementSignStep.when(context);

        assertFalse(result);
    }

    @Test
    void shouldRequestAgreementSignedFieldWhenNotProvided() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSessionData(builder -> builder.agreementSigned(false));

        var stepResult = agreementSignStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(
                updatedContext
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.AGREEMENT_SIGNED));
    }

    @Test
    void shouldCaptureAgreementSignedFieldWhenProvided() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSessionData(builder -> builder.agreementSigned(false))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(RequiredField.AGREEMENT_SIGNED, "true")));

        var stepResult = agreementSignStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertTrue(updatedContext.session().sessionData().agreementSigned());
    }
}
