package com.aterrizar.http.config;

import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.service.annotation.HttpExchange;

public class HttpExchangeScanner {
  private HttpExchangeScanner() {}

  public static List<Class<?>> findHttpExchangeInterfaces(String basePackage) {
    Reflections projectReflected =
        new Reflections(
            new ConfigurationBuilder()
                .forPackage(basePackage)
                .addScanners(Scanners.TypesAnnotated));

    Set<Class<?>> annotatedClasses = projectReflected.getTypesAnnotatedWith(HttpExchange.class);

    return annotatedClasses.stream().filter(Class::isInterface).toList();
  }
}
