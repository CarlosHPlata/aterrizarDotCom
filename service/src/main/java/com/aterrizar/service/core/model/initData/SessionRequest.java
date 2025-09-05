package com.aterrizar.service.core.model.initData;

import com.neovisionaries.i18n.CountryCode;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record SessionRequest(
    List<String> flights,
    CountryCode countryCode,
    int passengers,
    UUID userId
) {
}
