package com.aterrizar.service.core.model.session;

import java.io.Serializable;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;

@Builder
public record Airport(CountryCode countryCode, String airportCode) implements Serializable {}
