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
import com.aterrizar.service.checkin.steps.FundsCheckStep;
import com.aterrizar.service.checkin.steps.GetSessionStep;
import com.aterrizar.service.checkin.steps.PassportInformationStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.aterrizar.service.checkin.steps.ValidateSessionStep;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;
import mocks.MockFlowExecutor;

@ExtendWith(MockitoExtension.class)
public class VEContinueFlowTest {
    @Mock private GetSessionStep getSessionStep;
    @Mock private ValidateSessionStep validateSessionStep;
    @Mock private FundsCheckStep fundsCheckStep;
    @Mock private PassportInformationStep passportInformationStep;
    @Mock private AgreementSignStep agreementSignStep;
    @Mock private SaveSessionStep saveSessionStep;
    @Mock private CompleteCheckinStep completeCheckinStep;
    
    @InjectMocks private VEContinueFlow veContinueFlow;

    @Test
    void shouldReturnTheListOfValidSteps() {
        var context = MockContext.initializedMock(CountryCode.AD);
        var flowExecutor = new MockFlowExecutor();
        veContinueFlow.flow(flowExecutor).execute(context);

        assertEquals(6, flowExecutor.getExecutedSteps().size());
        assertEquals(
                List.of(
                        "GetSessionStep",
                        "ValidateSessionStep",
                        "FundsCheckStep",
                        "PassportInformationStep",
                        "AgreementSignStep",
                        "CompleteCheckinStep"),
                flowExecutor.getExecutedSteps());
    }






    
}
