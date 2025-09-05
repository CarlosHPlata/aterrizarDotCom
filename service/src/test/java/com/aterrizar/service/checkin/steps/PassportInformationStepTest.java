package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

class PassportInformationStepTest {

  private PassportInformationStep passportInformationStep;

  @BeforeEach
  void setUp() {
    passportInformationStep = new PassportInformationStep();
  }

  @Test
  void shouldExecuteWhenPassportIsNotSetInSession() {
    var context =
        MockContext.initializedMock(CountryCode.AD)
            .withUserInformation(builder -> builder.passportNumber(null));

    var result = passportInformationStep.when(context);
    assertTrue(result);
  }

  @Test
  void shouldNotExecuteWhenPassportIsSet() {
    var context =
        MockContext.initializedMock(CountryCode.AD)
            .withUserInformation(builder -> builder.passportNumber("123456789"));

    var result = passportInformationStep.when(context);
    assertFalse(result);
  }

  @Test
  void shouldRequestThePassportToTheUserWhenIsNotSet() {
    var context =
        MockContext.initializedMock(CountryCode.AD)
            .withUserInformation(builder -> builder.passportNumber(null));

    var stepResult = passportInformationStep.onExecute(context);
    var updatedContext = stepResult.context();

    assertTrue(stepResult.isSuccess());
    assertTrue(stepResult.isTerminal());
    assertTrue(
        updatedContext.checkinResponse().providedFields().contains(RequiredField.PASSPORT_NUMBER));
  }

  @Test
  void shouldCaptureThePassportWhenIsProvided() {
    var passportNo = "123456789";

    var context =
        MockContext.initializedMock(CountryCode.US)
            .withUserInformation(builder -> builder.passportNumber(null))
            .withCheckinRequest(
                requestBuilder ->
                    requestBuilder.providedFields(
                        Map.of(RequiredField.PASSPORT_NUMBER, passportNo)));

    var stepResult = passportInformationStep.onExecute(context);
    var updatedContext = stepResult.context();
    assertTrue(stepResult.isSuccess());
    assertFalse(stepResult.isTerminal());
    assertEquals(passportNo, updatedContext.userInformation().passportNumber());
  }
}
