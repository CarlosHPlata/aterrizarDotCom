package com.aterrizar.service.checkin.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.steps.CompleteInitStep;
import com.aterrizar.service.checkin.steps.CreateBaseSessionDataStep;
import com.aterrizar.service.checkin.steps.CreateBaseSessionStep;
import com.aterrizar.service.checkin.steps.FillExperimentsStep;
import com.aterrizar.service.checkin.steps.RetrieveFlightsDataStep;
import com.aterrizar.service.checkin.steps.SaveSessionStep;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;
import mocks.MockFlowExecutor;

@ExtendWith(MockitoExtension.class)
class GeneralInitFlowTest {
    @Mock private CreateBaseSessionStep createBaseSessionStep;
    @Mock private CreateBaseSessionDataStep createBaseSessionDataStep;
    @Mock private RetrieveFlightsDataStep retrieveFlightsDataStep;
    @Mock private SaveSessionStep saveSessionStep;
    @Mock private CompleteInitStep completeInitStep;
    @Mock private FillExperimentsStep fillExperimentsStep;
    @InjectMocks private GeneralInitFlow generalInitFlow;

    @Test
    void shouldReturnTheListOfValidSteps() {
        var expectedSteps =
                List.of(
                        "CreateBaseSessionStep",
                        "CreateBaseSessionDataStep",
                        "FillExperimentsStep",
                        "RetrieveFlightsDataStep",
                        "SaveSessionStep",
                        "CompleteInitStep");
        var context = MockContext.initializedMock(CountryCode.AD);
        var flowExecutor = new MockFlowExecutor();
        generalInitFlow.flow(flowExecutor).execute(context);

        assertEquals(expectedSteps.size(), flowExecutor.getExecutedSteps().size());
        assertEquals(expectedSteps, flowExecutor.getExecutedSteps());
    }
}
