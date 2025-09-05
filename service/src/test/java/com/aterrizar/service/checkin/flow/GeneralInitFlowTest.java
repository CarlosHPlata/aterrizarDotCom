package com.aterrizar.service.checkin.flow;

import com.aterrizar.service.checkin.steps.*;
import com.neovisionaries.i18n.CountryCode;
import mocks.MockContext;
import mocks.MockFlowExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeneralInitFlowTest {
    @Mock
    private CreateBaseSessionStep createBaseSessionStep;
    @Mock
    private CreateBaseSessionDataStep createBaseSessionDataStep;
    @Mock
    private SaveSessionStep saveSessionStep;
    @Mock
    private CompleteInitStep completeInitStep;
    @InjectMocks
    private GeneralInitFlow generalInitFlow;

    @Test
    void shouldReturnTheListOfValidSteps() {
        var context = MockContext.initializedMock(CountryCode.AD);
        var flowExecutor = new MockFlowExecutor();
        generalInitFlow.flow(flowExecutor)
                .execute(context);

        assertEquals(4, flowExecutor.getExecutedSteps().size());
        assertEquals(
                List.of(
                        "CreateBaseSessionStep",
                        "CreateBaseSessionDataStep",
                        "SaveSessionStep",
                        "CompleteInitStep"
                ),
                flowExecutor.getExecutedSteps()
        );
    }
}