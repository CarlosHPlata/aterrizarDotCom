package com.aterrizar.service.checkin.steps;

import com.aterrizar.service.core.model.session.UserInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.scanner.IdScanProviderFactory;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.scanner.ScannerGateway;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdScanValidationStep implements Step {

    private final IdScanProviderFactory providerFactory;

    @Override
    public boolean when(Context context) {
        var userInfo = Optional.ofNullable(context.session().userInformation());

        boolean hasDocument = userInfo.map(UserInformation::documentId).isPresent();
        boolean hasScanToken = userInfo.map(UserInformation::scanToken).isPresent();
        boolean hasRetries = userInfo
                .map(UserInformation::idScanRetries)
                .filter(r -> r > 0)
                .isPresent();

        return !hasDocument && !hasScanToken && !hasRetries;
    }

    @Override
    public StepResult onExecute(Context context) {

        String countryCode = context.countryCode().getAlpha2();
        ScannerGateway provider = providerFactory.getProvider(countryCode);
        String token = provider.getToken();

        var updatedContext = context
                .withUserInformation(builder -> builder.scanToken(token))
                .withRequiredField(RequiredField.SCAN_TOKEN)
                .withRequiredField(RequiredField.DOCUMENT_ID);

        return StepResult.terminal(updatedContext);
    }
}
