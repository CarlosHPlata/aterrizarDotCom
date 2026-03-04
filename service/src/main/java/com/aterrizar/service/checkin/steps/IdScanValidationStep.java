package com.aterrizar.service.checkin.steps;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.scanner.IdScanProviderFactory;
import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.RequiredField;
import com.aterrizar.service.external.ScannerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdScanValidationStep implements Step {

    private final IdScanProviderFactory idScanProviderFactory;
    private final ScannerGateway scannerGateway;

    @Override
    public boolean when(Context context) {
        return context.checkinRequest() == null
                || !context.checkinRequest().providedFields().containsKey(RequiredField.SCAN_TOKEN);
    }

    @Override
    public StepResult onExecute(Context context) {

        String countryCode = context.countryCode().getAlpha2();

        String expectedPrefix = idScanProviderFactory.getExpectedPrefix(countryCode);
        String provider =
                expectedPrefix.equals(IdScanProviderFactory.ONFIDO_PREFIX) ? "ofido" : "jumio";

        String generatedToken = scannerGateway.generateToken(provider);

        Context updatedContext =
                context.withRequiredField(RequiredField.SCAN_TOKEN)
                        .withRequiredField(RequiredField.DOCUMENT_ID);

        return StepResult.terminal(updatedContext);
    }
}
