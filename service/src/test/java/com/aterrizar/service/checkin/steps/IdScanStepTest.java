package com.aterrizar.service.checkin.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.checkin.scanner.DocumentValidator;
import com.aterrizar.service.checkin.scanner.IdScanProviderFactory;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.scanner.ScanValidationStatus;
import com.aterrizar.service.external.scanner.ScannerGateway;
import com.neovisionaries.i18n.CountryCode;

import mocks.MockContext;

@ExtendWith(MockitoExtension.class)
class IdScanStepTest {

    @Mock private IdScanProviderFactory providerFactory;
    @Mock private DocumentValidator documentValidator;
    @Mock private ScannerGateway scannerGateway;

    private IdScanStep idScanStep;

    @BeforeEach
    void setUp() {
        idScanStep = new IdScanStep(providerFactory, documentValidator);
    }

    // --- when() ---

    @Test
    void shouldExecuteWhenScanTokenExistsAndRetriesBelowMax() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(
                                builder -> builder.scanToken("ON-TOKEN123").idScanRetries(0));

        assertTrue(idScanStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenNoScanTokenInSession() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken(null));

        assertFalse(idScanStep.when(context));
    }

    @Test
    void shouldNotExecuteWhenRetriesReachMax() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(
                                builder ->
                                        builder.scanToken("ON-TOKEN123")
                                                .idScanRetries(IdScanStep.MAX_RETRIES));

        var result = idScanStep.execute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertTrue(result.message().contains("406"));
    }

    // --- onExecute() ---

    @Test
    void shouldReturnFailureWhenTokenIsMissingFromRequest() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken("ON-TOKEN123"))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.DOCUMENT_ID,
                                                        "ON-DOC1234567")));

        var result = idScanStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertTrue(result.message().contains("400"));
    }

    @Test
    void shouldReturnFailureWhenDocumentIdIsMissingFromRequest() {
        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken("ON-TOKEN123"))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(RequiredField.SCAN_TOKEN, "ON-TOKEN123")));

        var result = idScanStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.isTerminal());
        assertTrue(result.message().contains("400"));
    }

    @Test
    void shouldSucceedAndClearRetriesOnSuccessStatus() {
        var token = "ON-TOKEN123";
        var documentId = "ON-DOC1234567".substring(0, 12);

        when(providerFactory.getProvider("US")).thenReturn(scannerGateway);
        when(documentValidator.validate(scannerGateway, token, documentId))
                .thenReturn(ScanValidationStatus.SUCCESS);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken(token).idScanRetries(1))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.SCAN_TOKEN, token,
                                                        RequiredField.DOCUMENT_ID, documentId)));

        var result = idScanStep.onExecute(context);

        assertTrue(result.isSuccess());
        assertFalse(result.isTerminal());
        assertEquals(0, result.context().session().userInformation().idScanRetries());
    }

    @Test
    void shouldIncrementRetriesAndReturnTerminalOnPendingStatus() {
        var token = "ON-TOKEN120";
        var documentId = "ON-DOC1234567".substring(0, 12);

        when(providerFactory.getProvider("US")).thenReturn(scannerGateway);
        when(documentValidator.validate(scannerGateway, token, documentId))
                .thenReturn(ScanValidationStatus.PENDING);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken(token).idScanRetries(1))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.SCAN_TOKEN, token,
                                                        RequiredField.DOCUMENT_ID, documentId)));

        var result = idScanStep.onExecute(context);

        assertTrue(result.isSuccess());
        assertTrue(result.isTerminal());
        assertEquals(2, result.context().session().userInformation().idScanRetries());
    }

    @Test
    void shouldReturnFailureOnRejectedStatus() {
        var token = "ON-TOKEN122";
        var documentId = "ON-DOC1234567".substring(0, 12);

        when(providerFactory.getProvider("US")).thenReturn(scannerGateway);
        when(documentValidator.validate(scannerGateway, token, documentId))
                .thenReturn(ScanValidationStatus.REJECTED);

        var context =
                MockContext.initializedMock(CountryCode.US)
                        .withUserInformation(builder -> builder.scanToken(token))
                        .withCheckinRequest(
                                builder ->
                                        builder.providedFields(
                                                Map.of(
                                                        RequiredField.SCAN_TOKEN, token,
                                                        RequiredField.DOCUMENT_ID, documentId)));

        var result = idScanStep.onExecute(context);

        assertFalse(result.isSuccess());
        assertTrue(result.message().contains("406"));
    }
}
