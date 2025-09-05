package com.aterrizar.service.checkin.flow;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.steps.*;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;
import mocks.MockFlowExecutor;

@ExtendWith(MockitoExtension.class)
class GeneralCheckinFlowTest {
  @Mock private GetSessionStep getSessionStep;
  @Mock private ValidateSessionStep validateSessionStep;
  @Mock private PassportInformationStep passportInformationStep;
  @Mock private AgreementSignStep agreementSignStep;
  @Mock private SaveSessionStep saveSessionStep;
  @Mock private CompleteCheckinStep completeCheckinStep;
  @InjectMocks private GeneralCheckinFlow generalCheckinFlow;

  @Test
  void shouldReturnTheListOfValidSteps() {
    var context = MockContext.initializedMock(CountryCode.AD);
    var flowExecutor = new MockFlowExecutor();
    generalCheckinFlow.flow(flowExecutor).execute(context);

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
