package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.feature.EtaFeature;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.session.Session;
import com.aterrizar.service.core.model.session.UserInformation;
import com.aterrizar.service.external.HomeOfficeGateway;
import com.neovisionaries.i18n.CountryCode;

@ExtendWith(MockitoExtension.class)
class EtaValidationStepTest {

    @Mock private HomeOfficeGateway homeOfficeGateway;
    @Mock private Context context;
    @Mock private Session session;
    @Mock private UserInformation userInformation;
    @Mock private EtaFeature etaFeature;

    private EtaValidationStep etaValidationStep;

    @BeforeEach
    void setUp() {
        etaValidationStep = new EtaValidationStep(etaFeature, homeOfficeGateway);

        lenient().when(etaFeature.isCountryAvailable(anyString())).thenReturn(true);
        lenient().when(context.session()).thenReturn(session);
        lenient().when(session.userInformation()).thenReturn(userInformation);
    }

    @ParameterizedTest(name = "ETA Rejected")
    @EnumSource(
            value = CountryCode.class,
            names = {"GB", "CH", "SE"})
    void shouldReturnFailureWhenApiReturnsRejected(CountryCode countryCode) {
        when(context.countryCode()).thenReturn(countryCode);
        when(userInformation.passportNumber()).thenReturn("MEX9876541");
        when(homeOfficeGateway.validateEta(anyString(), anyString())).thenReturn("Rejected");

        StepResult result = etaValidationStep.onExecute(context);
        assertFalse(result.isSuccess());
    }

    @ParameterizedTest(name = "ETA Pending")
    @EnumSource(
            value = CountryCode.class,
            names = {"GB", "CH", "SE"})
    void shouldMarkSessionAsPendingWhenApiReturnsPending(CountryCode countryCode) {
        when(context.countryCode()).thenReturn(countryCode);
        when(userInformation.passportNumber()).thenReturn("MEX9876542");
        when(homeOfficeGateway.validateEta(anyString(), anyString())).thenReturn("Pending");

        when(context.withSessionData(any())).thenReturn(context);

        StepResult result = etaValidationStep.onExecute(context);

        assertTrue(result.isSuccess());
        verify(context).withSessionData(any());
    }

    @ParameterizedTest(name = "ETA Accepted")
    @EnumSource(
            value = CountryCode.class,
            names = {"GB", "CH", "SE"})
    void shouldNotMarkManualReviewWhenApiReturnsAccepted(CountryCode countryCode) {
        when(context.countryCode()).thenReturn(countryCode);
        when(userInformation.passportNumber()).thenReturn("MEX9876540");
        when(homeOfficeGateway.validateEta(anyString(), anyString())).thenReturn("Accepted");

        StepResult result = etaValidationStep.onExecute(context);

        assertTrue(result.isSuccess());
        verify(context, never()).withSessionData(any());
    }

    @Test
    void shouldSkipValidationForNonEnabledCountries() {
        when(context.countryCode()).thenReturn(CountryCode.US);
        when(etaFeature.isCountryAvailable("US")).thenReturn(false);

        boolean shouldExecute = etaValidationStep.when(context);

        assertFalse(shouldExecute);
        verifyNoInteractions(homeOfficeGateway);
    }
}
