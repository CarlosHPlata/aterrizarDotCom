package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.model.InitContext;
import com.aterrizar.service.core.model.init.SessionRequest;
import com.aterrizar.service.external.PassportGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

@ExtendWith(MockitoExtension.class)
class PassportValidationStepTest {

    @Mock private PassportGateway mockPassportGateway;

    @InjectMocks private PassportValidationStep passportValidationStep;

    @Test
    void shouldExecuteWhenInitContextHasSessionRequest() {
        var mockInitContext = mock(InitContext.class);
        var mockSessionRequest = mock(SessionRequest.class);

        when(mockInitContext.sessionRequest()).thenReturn(Optional.of(mockSessionRequest));
    void shouldExecuteWhenInitContextHasSessionRequest() {
        var mockInitContext = mock(InitContext.class);
        var mockSessionRequest = mock(SessionRequest.class);

        when(mockInitContext.sessionRequest()).thenReturn(Optional.of(mockSessionRequest));

        var result = passportValidationStep.when(mockInitContext);

        var result = passportValidationStep.when(mockInitContext);

        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenInitContextHasNoSessionRequest() {
        var mockInitContext = mock(InitContext.class);

        when(mockInitContext.sessionRequest()).thenReturn(Optional.empty());

        var result = passportValidationStep.when(mockInitContext);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenPassportIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withUserInformation(builder -> builder.passportNumber(null));

        var stepResult = passportValidationStep.onExecute(context);

        assertFalse(stepResult.isSuccess());
        assertEquals("Passport number is required.", stepResult.message());
        verify(mockPassportGateway, never()).validate(anyString());
    }

    @Test
    void shouldFailWhenPassportIsBlank() {
        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withUserInformation(builder -> builder.passportNumber("   "));

        var stepResult = passportValidationStep.onExecute(context);

        assertFalse(stepResult.isSuccess());
        assertEquals("Passport number is required.", stepResult.message());
        verify(mockPassportGateway, never()).validate(anyString());
        verify(mockPassportGateway, never()).validate(anyString());
    }

    @Test
    void shouldFailWhenPassportIsInvalidAccordingToGateway() {
        var invalidPassport = "P7399";
        when(mockPassportGateway.validate(invalidPassport)).thenReturn(false);

        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withUserInformation(builder -> builder.passportNumber(invalidPassport));

        var stepResult = passportValidationStep.onExecute(context);

        assertFalse(stepResult.isSuccess());
        assertEquals("Invalid passport number", stepResult.message());
        verify(mockPassportGateway).validate(invalidPassport);
        verify(mockPassportGateway).validate(invalidPassport);
    }

    @Test
    void shouldSucceedWhenPassportIsValidAccordingToGateway() {
        var validPassport = "G3567";
        when(mockPassportGateway.validate(validPassport)).thenReturn(true);

        var context =
                MockContext.initializedMock(CountryCode.AD)
                        .withUserInformation(builder -> builder.passportNumber(validPassport));

        var stepResult = passportValidationStep.onExecute(context);

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        verify(mockPassportGateway).validate(validPassport);
        assertFalse(stepResult.isTerminal());
        verify(mockPassportGateway).validate(validPassport);
    }

}