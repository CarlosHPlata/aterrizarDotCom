package com.aterrizar.service.checkin.steps;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.feature.EtaFeature;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.external.HomeOfficeGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtaValidationStep implements Step {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_REJECTED = "Rejected";
    private static final String ETA_VALIDATION_REJECTION_MESSAGE =
            "ETA validation rejected by Home Office";
    private static final String ETA_VALIDATION_STEP_FAILURE_MESSAGE =
            "Fallo técnico en validación ETA: ";

    private final EtaFeature etaFeature;
    private final HomeOfficeGateway homeOfficeGateway;

    @Override
    public boolean when(Context context) {
        return isCountryEnabled(context) && hasPassport(context);
    }

    @Override
    public StepResult onExecute(Context context) {
        var userInfo = context.session().userInformation();
        String passportNumber = userInfo.passportNumber();
        String destinationCode = context.countryCode().name();

        try {
            String status = homeOfficeGateway.validateEta(passportNumber, destinationCode);

            if (STATUS_REJECTED.equalsIgnoreCase(status)) {
                return StepResult.failure(context, ETA_VALIDATION_REJECTION_MESSAGE);
            }

            if (STATUS_PENDING.equalsIgnoreCase(status)) {
                return StepResult.success(markManualReviewRequired(context));
            }

            return StepResult.success(context);

        } catch (Exception e) {
            return StepResult.failure(
                    context, ETA_VALIDATION_STEP_FAILURE_MESSAGE + e.getMessage());
        }
    }

    private boolean isCountryEnabled(Context context) {
        return Optional.ofNullable(context.countryCode())
                .map(code -> etaFeature.isCountryAvailable(code.name()))
                .orElse(false);
    }

    private boolean hasPassport(Context context) {
        return Optional.ofNullable(context.session().userInformation())
                .map(info -> info.passportNumber() != null)
                .orElse(false);
    }

    private Context markManualReviewRequired(Context context) {
        return context.withSessionData(builder -> builder.etaManualReviewRequired(true));
    }
}
