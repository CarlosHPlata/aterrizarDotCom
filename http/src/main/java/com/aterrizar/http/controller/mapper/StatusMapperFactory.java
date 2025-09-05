package com.aterrizar.http.controller.mapper;

import java.util.List;

import com.aterrizar.http.controller.mapper.status.Complete;
import com.aterrizar.http.controller.mapper.status.Rejected;
import com.aterrizar.http.controller.mapper.status.StatusMapperTemplate;
import com.aterrizar.http.controller.mapper.status.UserInputRequired;

public class StatusMapperFactory {
  public static StatusMapperTemplate getMapperByStatusCode(String statusCode) {
    List<StatusMapperTemplate> mappers = getAllMappers();
    for (StatusMapperTemplate mapper : mappers) {
      if (mapper.getStatus().name().equals(statusCode)) {
        return mapper;
      }
    }
    throw new IllegalArgumentException("No mapper found for status code: " + statusCode);
  }

  private static List<StatusMapperTemplate> getAllMappers() {
    return List.of(new Complete(), new Rejected(), new UserInputRequired());
  }
}
