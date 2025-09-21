package com.aterrizar.service.checkin.steps;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aterrizar.service.core.framework.flow.Step;
import com.aterrizar.service.core.framework.flow.StepResult;
import com.aterrizar.service.core.model.Context;

@Service
@SuppressWarnings("deprecation")
public class PassportValidationStep implements Step {

    private static final Logger logger = LoggerFactory.getLogger(PassportValidationStep.class);

    @Override
    public boolean when(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();

        return Optional.ofNullable(userInfo).isPresent()
                && Optional.ofNullable(userInfo.passportNumber()).isPresent();
    }

    @Override
    public StepResult onExecute(Context context) {
        var session = context.session();
        var userInfo = session.userInformation();
        String passportNumber = userInfo.passportNumber();
        logger.info("Validating passport number: {}", passportNumber);

        if (passportNumber == null || passportNumber.isBlank()) {
            var updatedContext = context.withSessionData(builder -> 
                builder.passportValidationCleared(false));
            return StepResult.failure(updatedContext, "Passport number is required.");
        }

        try {
            String urlString = "http://localhost:3001/personagov/api/v1/passport?p=" + passportNumber;
            URL url = new URL(urlString);

            long responseTime = System.currentTimeMillis();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            int responseCode = connection.getResponseCode();

            long duration = System.currentTimeMillis() - responseTime;

            if (responseCode == 200) {
                logger.info("Passport validation succeeded in {} ms", duration);
                var updatedContext = context.withSessionData(builder ->
                    builder.passportValidationCleared(true));
                return StepResult.success(updatedContext);

            } else if (responseCode == 406) {
                logger.warn("Passport validation failed in {} ms", duration);
                var updatedContext = context.withSessionData(builder ->
                    builder.passportValidationCleared(false));
                return StepResult.failure(updatedContext, "Invalid passport number");

            } else {
                logger.error("Passport validation failed in {} ms with response code {}", duration, responseCode);
                return StepResult.failure(context, "Passport validation service error");
            }
        } catch (IOException e) {
                logger.error("IOException during passport validation", e.getMessage(), e);
                return StepResult.failure(context, "Invalid passport number format");
        }
    }
}
