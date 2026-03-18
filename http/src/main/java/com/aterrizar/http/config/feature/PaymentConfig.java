package com.aterrizar.http.config.feature;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aterrizar.service.checkin.feature.PaymentFeature;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "feature.tax")
public class PaymentConfig implements PaymentFeature {

    private Map<String, List<String>> payments;

    @Override
    public List<String> getAllowedMethods(String countryCode) {
        if (payments == null || !payments.containsKey(countryCode)) {
            return Collections.emptyList();
        }
        return List.copyOf(payments.get(countryCode));
    }
}
