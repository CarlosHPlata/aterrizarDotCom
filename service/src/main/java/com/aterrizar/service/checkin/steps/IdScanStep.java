package com.aterrizar.service.checkin.steps;

import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.ValidationStatus;
import com.aterrizar.service.external.ScannerGateway;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class IdScanStep implements Step {
    private static final int MAX_RETRIES = 3;
    private final ScannerGateway scanner;

    @Override
    public StepResult onExecute(Context context) {
        var sessionData = context.session().sessionData();
        var request = context.checkinRequest();

        var scanToken = request.providedFields().get(RequiredField.SCAN_TOKEN);
        var documentId = request.providedFields().get(RequiredField.DOCUMENT_ID);

        if (sessionData.scanRetryCount() >= MAX_RETRIES - 1) {
            return StepResult.success(context);
        }

        if (scanToken == null || documentId == null) {
            return StepResult.failure(context, "Token and document ID are required");
        }

        var status = scanner.validateDocument(scanToken, documentId, sessionData.countryCode());

        Map<ValidationStatus, Function<Context, StepResult>> handlers =
                Map.of(
                        ValidationStatus.SUCCESS,
                                ctx ->
                                        StepResult.success(
                                                ctx.withSessionData(
                                                        s -> s.scanToken(null).scanRetryCount(-1))),
                        ValidationStatus.PENDING,
                                ctx ->
                                        StepResult.terminal(
                                                ctx.withSessionData(
                                                                s ->
                                                                        s.scanRetryCount(
                                                                                sessionData
                                                                                                .scanRetryCount()
                                                                                        + 1))
                                                        .withRequiredField(RequiredField.SCAN_TOKEN)
                                                        .withRequiredField(
                                                                RequiredField.DOCUMENT_ID)),
                        ValidationStatus.REJECTED,
                                ctx -> StepResult.failure(ctx, "Document rejected"));

        return handlers.get(status).apply(context);
    }
}
