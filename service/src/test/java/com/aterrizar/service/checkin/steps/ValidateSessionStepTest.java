package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class ValidateSessionStepTest {
    private ValidateSessionStep validateSessionStep;

    @BeforeEach
    void setUp() {
        validateSessionStep = new ValidateSessionStep();
    }

    @Test
    void shouldRejectIfUserDontMatch() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withCheckinRequest(
                                builder ->
                                        builder.countryCode(CountryCode.US)
                                                .userId(UUID.randomUUID()))
                        .withUserInformation(builder -> builder.userId(UUID.randomUUID()));

        var result = validateSessionStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertEquals("User ID does not match session", result.message());
    }

    @Test
    void shouldRejectIfCountryDontMatch() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withCheckinRequest(builder -> builder.countryCode(CountryCode.AR));

        var result = validateSessionStep.onExecute(context);
        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertEquals("Country code does not match session", result.message());
    }

    @Test
    void shouldPassIfAllMatch() {
        var userId = UUID.randomUUID();
        var country = CountryCode.US;

        var context =
                MockContext.initializedMock(country)
                        .withCheckinRequest(builder -> builder.countryCode(country).userId(userId))
                        .withUserInformation(builder -> builder.userId(userId));

        var result = validateSessionStep.onExecute(context);
        assertTrue(result.isSuccess());
        assertFalse(result.isTerminal());
    }
}
