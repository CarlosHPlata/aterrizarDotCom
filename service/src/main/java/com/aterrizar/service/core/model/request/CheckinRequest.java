package com.aterrizar.service.core.model.request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.aterrizar.service.core.model.RequiredField;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class CheckinRequest {
  private final UUID sessionId;
  private final UUID userId;
  private final CountryCode countryCode;
  @Builder.Default private Map<RequiredField, String> providedFields = new HashMap<>();
}
