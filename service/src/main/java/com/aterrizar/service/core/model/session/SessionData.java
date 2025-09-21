package com.aterrizar.service.core.model.session;

import java.io.Serializable;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;

@Builder(toBuilder = true)
public record SessionData(
        CountryCode countryCode, int passengers, boolean agreementSigned, List<FlightData> flights)
        implements Serializable {}
