package com.aterrizar.service.checkin.feature;

import java.util.List;

public interface PaymentFeature {
    List<String> getAllowedMethods(String countryCode);
}
