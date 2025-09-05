package com.aterrizar.service.core.model.session;

import com.neovisionaries.i18n.CountryCode;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record SessionData(
        CountryCode countryCode,
        int passengers,
        boolean agreementSigned,
        List<FlightData> flights
) {

}
