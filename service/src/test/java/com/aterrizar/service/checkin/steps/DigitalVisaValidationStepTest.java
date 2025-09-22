package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.config.DigitalVisaProperties;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

@ExtendWith(MockitoExtension.class)
class DigitalVisaValidationStepTest {

    @Mock
    private DigitalVisaProperties digitalVisaProperties;

    private DigitalVisaValidationStep digitalVisaValidationStep;

    @BeforeEach
    void setUp() {
        digitalVisaValidationStep = new DigitalVisaValidationStep(digitalVisaProperties);
    }

    @Test
    void shouldNotExecuteWhenUserAlreadyHasVisaNumber() {
        // No mock needed - step returns early when user has visa number
        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber("VISA123456"));

        var result = digitalVisaValidationStep.when(context);

        assertFalse(result);
    }

    @Test
    void shouldNotExecuteWhenNoFlightDestinationsRequireVisa() {
        when(digitalVisaProperties.isDigitalVisaRequired("GB")).thenReturn(false);

        // Flight from US to GB (not requiring visa)
        var flightData = FlightData.builder()
                .flightNumber("USJFKGBLHR")
                .departure(Airport.builder()
                        .airportCode("JFK")
                        .countryCode(CountryCode.US)
                        .build())
                .destination(Airport.builder()
                        .airportCode("LHR")
                        .countryCode(CountryCode.GB)
                        .build())
                .build();

        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withSessionData(builder -> builder.flights(List.of(flightData)));

        var result = digitalVisaValidationStep.when(context);

        assertFalse(result);
    }

    @Test
    void shouldExecuteWhenFlightDestinationRequiresVisa() {
        when(digitalVisaProperties.isDigitalVisaRequired("IN")).thenReturn(true);

        // Flight from US to India (requiring visa)
        var flightData = FlightData.builder()
                .flightNumber("USJFKINDEL")
                .departure(Airport.builder()
                        .airportCode("JFK")
                        .countryCode(CountryCode.US)
                        .build())
                .destination(Airport.builder()
                        .airportCode("DEL")
                        .countryCode(CountryCode.IN)
                        .build())
                .build();

        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withSessionData(builder -> builder.flights(List.of(flightData)));

        var result = digitalVisaValidationStep.when(context);

        assertTrue(result);
    }

    @Test
    void shouldExecuteWhenAnyFlightDestinationRequiresVisa() {
        when(digitalVisaProperties.isDigitalVisaRequired("GB")).thenReturn(false);
        when(digitalVisaProperties.isDigitalVisaRequired("AU")).thenReturn(true);

        // Multiple flights, one requiring visa
        var flight1 = FlightData.builder()
                .flightNumber("USJFKGBLHR")
                .departure(Airport.builder()
                        .airportCode("JFK")
                        .countryCode(CountryCode.US)
                        .build())
                .destination(Airport.builder()
                        .airportCode("LHR")
                        .countryCode(CountryCode.GB)
                        .build())
                .build();

        var flight2 = FlightData.builder()
                .flightNumber("GBLHRAUSYD")
                .departure(Airport.builder()
                        .airportCode("LHR")
                        .countryCode(CountryCode.GB)
                        .build())
                .destination(Airport.builder()
                        .airportCode("SYD")
                        .countryCode(CountryCode.AU)
                        .build())
                .build();

        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withSessionData(builder -> builder.flights(List.of(flight1, flight2)));

        var result = digitalVisaValidationStep.when(context);

        assertTrue(result);
    }

    @Test
    void shouldNotExecuteWhenSessionDataIsNull() {
        // No mock needed - step returns early when sessionData is null
        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withSessionData(builder -> builder.flights(null));

        var result = digitalVisaValidationStep.when(context);

        assertFalse(result);
    }

    @Test
    void shouldRequestVisaNumberWhenNotProvided() {
        // No mock needed for onExecute test
        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null));

        var stepResult = digitalVisaValidationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(
                updatedContext
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.VISA_NUMBER));
    }

    @Test
    void shouldCaptureVisaNumberWhenProvided() {
        var visaNumber = "VISA123456";

        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withCheckinRequest(
                        builder -> builder.providedFields(
                                Map.of(RequiredField.VISA_NUMBER, visaNumber)));

        var stepResult = digitalVisaValidationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertEquals(visaNumber, updatedContext.userInformation().visaNumber());
    }

    @Test
    void shouldRequestVisaWhenNotInProvidedFields() {
        // Test when VISA_NUMBER is not in the providedFields map
        var context = MockContext.initializedMock(CountryCode.US)
                .withUserInformation(builder -> builder.visaNumber(null))
                .withCheckinRequest(
                        builder -> builder.providedFields(
                                Map.of(RequiredField.PASSPORT_NUMBER, "A12345678")));

        var stepResult = digitalVisaValidationStep.onExecute(context);

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(
                stepResult.context()
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.VISA_NUMBER));
    }
}