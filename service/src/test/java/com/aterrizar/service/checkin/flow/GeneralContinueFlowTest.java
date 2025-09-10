package com.aterrizar.service.checkin.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var context = MockContext.initializedMock(CountryCode.AD);
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
}
