package com.aterrizar.service.core.model.request;

import java.util.HashSet;
import java.util.Set;

import com.aterrizar.service.core.model.RequiredField;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@lombok.experimental.Accessors(fluent = true)
public class CheckinResponse {
  private Set<RequiredField> providedFields;
  @Nullable private String errorMessage;

  public CheckinResponse addProvidedField(RequiredField field) {
    if (providedFields == null) {
      providedFields = new HashSet<>();
    }
    providedFields.add(field);
    return this;
  }
}
