package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.aterrizar.service.checkin.config.HealthDeclarationProperties;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.session.Airport;
import com.aterrizar.service.core.model.session.FlightData;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

public class HealthDeclarationStepTest {

    @ParameterizedTest
    @EnumSource(
            value = CountryCode.class,
            names = {"CN", "US", "MX"})
    void shouldExecuteWhenDestinationIsTargetCountry(CountryCode country) {
        var healthDeclarationStep = createStepWithTargets(country);
        var context = createContextWithDestinations(country);
        var result = healthDeclarationStep.when(context);

        assertTrue(result);
    }

    @Test
    void shouldExecuteWhenAnyDestinationIsTargetCountry() {
        var healthDeclarationStep = createStepWithTargets(CountryCode.ES, CountryCode.US);
        var context = createContextWithDestinations(CountryCode.MX, CountryCode.CN, CountryCode.US);
        var result = healthDeclarationStep.when(context);

        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("nonTargetCountries")
    void shouldNotExecuteWhenDestinationsAreNotTargetCountry(
            CountryCode[] targets, CountryCode[] destinations) {

        var healthDeclarationStep = createStepWithTargets(targets);
        var context = createContextWithDestinations(destinations);
        var result = healthDeclarationStep.when(context);

        assertFalse(result);
    }

    private static Stream<Arguments> nonTargetCountries() {
        return Stream.of(
                Arguments.of(
                        new CountryCode[] {CountryCode.CN, CountryCode.ES},
                        new CountryCode[] {CountryCode.MX}),
                Arguments.of(
                        new CountryCode[] {CountryCode.CN, CountryCode.AU},
                        new CountryCode[] {CountryCode.US, CountryCode.IN}));
    }

    @Test
    void shouldRequestHealthClearAcknowledgementWhenNotProvided() {
        var healthDeclarationStep = createStepWithTargets(CountryCode.CN);
        var context = createContextWithDestinations(CountryCode.CN);

        var stepResult = healthDeclarationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isTerminal());
        assertTrue(stepResult.isSuccess());
        assertTrue(
                updatedContext
                        .checkinResponse()
                        .providedFields()
                        .contains(RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT));
    }

    @Test
    void shouldFailCheckinWhenInvalidHealthClearAcknowledgementProvided() {
        var healthDeclarationStep = createStepWithTargets(CountryCode.CN);
        var context =
                createContextWithDestinations(CountryCode.CN)
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT,
                                                        "false")));

        var stepResult = healthDeclarationStep.onExecute(context);

        assertTrue(stepResult.isTerminal());
        assertFalse(stepResult.isSuccess());
    }

    @Test
    void shouldCaptureHealthClearAcknowledgementFieldWhenProvided() {
        var healthDeclarationStep = createStepWithTargets(CountryCode.CN);
        var context =
                createContextWithDestinations(CountryCode.CN)
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.HEALTH_CLEAR_ACKNOWLEDGEMENT,
                                                        "true")));

        var stepResult = healthDeclarationStep.onExecute(context);
        var updatedContext = stepResult.context();

        assertTrue(stepResult.isSuccess());
        assertFalse(stepResult.isTerminal());
        assertTrue(updatedContext.session().sessionData().healthClearAcknowledgement());
    }

    private Step createStepWithTargets(CountryCode... targetCountries) {
        assertTrue(targetCountries.length > 0, "Target countries should not be empty");

        var properties = new HealthDeclarationProperties(targetCountries);
        return new HealthDeclarationStep(properties);
    }

    private Context createContextWithDestinations(CountryCode... destinations) {
        assertTrue(destinations.length > 0, "Destinations should not be empty");

        var flights =
                Arrays.stream(destinations)
                        .map(
                                code ->
                                        FlightData.builder()
                                                .destination(
                                                        Airport.builder().countryCode(code).build())
                                                .build())
                        .toList();

        return MockContext.initializedMock(CountryCode.US)
                .withSessionData(builder -> builder.flights(flights));
    }
}
