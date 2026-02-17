package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.aterrizar.service.core.model.session.ExperimentalData;
import com.aterrizar.service.external.ExperimentalGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

@ExtendWith(MockitoExtension.class)
class FillExperimentsStepTest {
    @Mock private ExperimentalGateway experimentalGateway;
    @Mock private Logger logger;
    @InjectMocks private FillExperimentsStep fillExperimentsStep;

    @Test
    void shouldNotExecuteWhenUserInfoIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withSession(builder -> builder.userInformation(null));
        assertThrows(NullPointerException.class, () -> fillExperimentsStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenEmailIsNull() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.email(null));
        assertFalse(fillExperimentsStep.when(context));
    }

    @Test
    void shouldFillExperimentsIfEmailIsExperimental() {
        var email = "test__agreementdrop@checkin.com";
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.email(email));
        var experimentalData =
                ExperimentalData.builder().experiments(List.of("agreementdrop")).build();

        when(experimentalGateway.getActiveExperiments(email)).thenReturn(experimentalData);

        var result = fillExperimentsStep.onExecute(context);
        var updatedContext = result.context();

        assertTrue(result.isSuccess());
        assertEquals(Optional.of(experimentalData), updatedContext.experimentalData());
    }
}
