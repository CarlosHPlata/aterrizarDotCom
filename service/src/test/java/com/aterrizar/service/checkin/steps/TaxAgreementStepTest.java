package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class TaxAgreementStepTest {

    private TaxAgreementStep taxAgreementStep;

    @BeforeEach
    void setUp() {
        taxAgreementStep = new TaxAgreementStep();
    }

    @Test
    void shouldExecuteWhenTaxAmountPresentAndDigitalSignIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(45.0).digitalSign(null));

        var result = taxAgreementStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldExecuteWhenTaxAmountPresentAndDigitalSignIsFalse() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(45.0).digitalSign(false));

        var result = taxAgreementStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenDigitalSignIsAlreadyTrue() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(45.0).digitalSign(true));

        var result = taxAgreementStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenTaxAmountIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(null).digitalSign(null));

        var result = taxAgreementStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldRequestDigitalSignFieldWhenNotProvided() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(45.0).digitalSign(null));

        var stepResult = taxAgreementStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(
                updatedContext
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.DIGITAL_SIGN));
    }

    @Test
    void shouldCaptureDigitalSignWhenProvidedAsTrue() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.taxAmount(45.0).digitalSign(null))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(RequiredField.DIGITAL_SIGN, "true")));

        var stepResult = taxAgreementStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertTrue(updatedContext.userInformation().digitalSign());
    }
}
