package com.aterrizar.service.checkin.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.steps.AgreementSignStep;
import com.aterrizar.service.checkin.steps.CompleteCheckinStep;
import com.aterrizar.service.checkin.steps.GetSessionStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.checkin.steps.ValidateSessionStep;
import com.aterrizar.service.core.model.ExperimentalStepKey;
import com.aterrizar.service.core.model.session.ExperimentalData;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;
import mocks.MockFlowExecutor;

@ExtendWith(MockitoExtension.class)
class GeneralContinueFlowTest {
    @Mock private GetSessionStep getSessionStep;
    @Mock private ValidateSessionStep validateSessionStep;
    @Mock private PassportInformationStep passportInformationStep;
    @Mock private AgreementSignStep agreementSignStep;
    @Mock private SaveSessionStep saveSessionStep;
    @Mock private CompleteCheckinStep completeCheckinStep;
    @InjectMocks private GeneralContinueFlow generalContinueFlow;

    @Test
    void shouldReturnTheListOfValidSteps() {
        var experimentalSteps =
                ExperimentalData.builder()
                        .experiments(List.of(ExperimentalStepKey.AGREEMENT_SIGN.value()))
                        .build();

        var context =
                MockContext.initializedMock(CountryCode.AD).withExperimentalData(experimentalSteps);

        when(agreementSignStep.when(context)).thenReturn(true);

        var flowExecutor = new MockFlowExecutor();
        generalContinueFlow.flow(flowExecutor).execute(context);

        assertEquals(5, flowExecutor.getExecutedSteps().size());
        assertEquals(
                List.of(
                        "GetSessionStep",
                        "ValidateSessionStep",
                        "PassportInformationStep",
                        "AgreementSignStep",
                        "CompleteCheckinStep"),
                flowExecutor.getExecutedSteps());
    }

    @Test
    void shouldSkipExperimentalStepIfNotActive() {
        var context = MockContext.initializedMock(CountryCode.AD);
        var flowExecutor = new MockFlowExecutor();
        generalContinueFlow.flow(flowExecutor).execute(context);

        assertEquals(4, flowExecutor.getExecutedSteps().size());
        assertEquals(
                List.of(
                        "GetSessionStep",
                        "ValidateSessionStep",
                        "PassportInformationStep",
                        "CompleteCheckinStep"),
                flowExecutor.getExecutedSteps());
    }
}
