package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.scanner.DocumentValidator;
import com.aterrizar.service.checkin.scanner.IdScanProviderFactory;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.core.model.request.CheckinRequest;
import com.aterrizar.service.core.model.session.UserInformation;
import com.aterrizar.service.external.scanner.ScanValidationStatus;
import com.aterrizar.service.external.scanner.ScannerGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IdScanStep implements Step {
    static final int MAX_RETRIES = 3;

    private final IdScanProviderFactory providerFactory;
    private final DocumentValidator documentValidator;

    @Override
    public boolean when(Context context) {
        return Optional.ofNullable(context.session().userInformation())
                .map(UserInformation::scanToken)
                .isPresent();
    }

    @Override
    public StepResult execute(Context context) {
        var userInfo = Optional.ofNullable(context.session().userInformation());
        boolean hasScanToken = userInfo.map(UserInformation::scanToken).isPresent();

        if (!hasScanToken) {
            return StepResult.success(context);
        }

        int retries = userInfo.map(UserInformation::resolvedRetries).orElse(0);
        if (retries >= MAX_RETRIES) {
            return StepResult.failure(context, "406: maximum retry attempts reached.");
        }

        return onExecute(context);
    }

    @Override
    public StepResult onExecute(Context context) {
        var request = context.checkinRequest();
        var userInfo = context.session().userInformation();

        String token = getField(request, RequiredField.SCAN_TOKEN);
        String documentId = getField(request, RequiredField.DOCUMENT_ID);

        if (token == null || documentId == null) {
            return StepResult.failure(context, "400: scan token and document ID are required.");
        }

        String countryCode = context.countryCode().getAlpha2();
        ScannerGateway provider = providerFactory.getProvider(countryCode);

        ScanValidationStatus status = documentValidator.validate(provider, token, documentId);

        return switch (status) {
            case SUCCESS -> {
                var updatedContext =
                        context.withUserInformation(
                                builder -> builder.documentId(documentId).idScanRetries(0));
                yield StepResult.success(updatedContext);
            }
            case PENDING -> {
                int retries = userInfo.resolvedRetries() + 1;
                var updatedContext =
                        context.withUserInformation(builder -> builder.idScanRetries(retries))
                                .withRequiredField(RequiredField.SCAN_TOKEN)
                                .withRequiredField(RequiredField.DOCUMENT_ID);
                yield StepResult.terminal(updatedContext);
            }
            case REJECTED -> StepResult.failure(context, "406: document verification rejected.");
        };
    }

    private String getField(CheckinRequest request, RequiredField field) {
        if (request == null || request.providedFields() == null) {
            return null;
        }
        return request.providedFields().get(field);
    }
}
