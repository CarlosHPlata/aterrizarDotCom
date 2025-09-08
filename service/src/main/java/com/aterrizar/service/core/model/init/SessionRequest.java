package com.aterrizar.service.core.model.init;

import java.util.List;
import java.util.UUID;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;

@Builder(toBuilder = true)
public record SessionRequest(
        List<String> flights, CountryCode countryCode, int passengers, UUID userId) {}
