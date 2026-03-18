package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.session.TaxData;
import com.aterrizar.service.external.TaxGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class TaxCalculationStepTest {

    private TaxGateway taxGateway;
    private TaxCalculationStep taxCalculationStep;

    @BeforeEach
    void setUp() {
        taxGateway = mock(TaxGateway.class);
        taxCalculationStep = new TaxCalculationStep(taxGateway);
    }

    @Test
    void shouldExecuteWhenRfcIsPresentAndTaxAmountIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(
                                builder -> builder.rfc("ABCD840101XYZ").taxAmount(null));

        var result = taxCalculationStep.when(context);
        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenTaxAmountIsAlreadyPresent() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(
                                builder -> builder.rfc("ABCD840101XYZ").taxAmount(45.0));

        var result = taxCalculationStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenRfcIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(null).taxAmount(null));

        var result = taxCalculationStep.when(context);
        assertFalse(result);
    }

    @Test
    void shouldCallGatewayAndUpdateTaxAmountWhenRfcIsPresent() {
        var rfc = "ABCD840101XYZ";
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(rfc).taxAmount(null));

        when(taxGateway.getTax(rfc)).thenReturn(new TaxData(45.0));

        var stepResult = taxCalculationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertEquals(45.0, updatedContext.userInformation().taxAmount());
    }

    @Test
    void shouldReturnFailureWhenGatewayThrowsException() {
        var rfc = "ABCD8401011";
        var context =
                MockContext.initializedMock(CountryCode.MX)
                        .withUserInformation(builder -> builder.rfc(rfc).taxAmount(null));

        when(taxGateway.getTax(rfc)).thenThrow(new RuntimeException("406 Not Acceptable"));

        var stepResult = taxCalculationStep.onExecute(context);

        assertFalse(stepResult.isSuccess());
        assertTrue(stepResult.isTerminal());
        assertEquals("Invalid RFC", stepResult.message());
    }
}
