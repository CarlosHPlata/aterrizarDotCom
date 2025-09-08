package com.aterrizar.http.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import jakarta.annotation.PostConstruct;

@Configuration
public class HttpClientConfig {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(HttpClientConfig.class);
  private static final String BASE_PACKAGE = "com.aterrizar.http.external.gateway";

  private final GenericApplicationContext context;
  private final Environment environment;

  public HttpClientConfig(GenericApplicationContext context, Environment environment) {
    this.context = context;
    this.environment = environment;
  }

  @PostConstruct
  public void registerClients() {
    var httpExchangeInterfaces = HttpExchangeScanner.findHttpExchangeInterfaces(BASE_PACKAGE);
    for (Class<?> httpExchangeInterface : httpExchangeInterfaces) {
      var beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanClass(httpExchangeInterface);
      beanDefinition.setInstanceSupplier(() -> createClient(httpExchangeInterface));

      var beanName = standardizeBeanName(httpExchangeInterface.getSimpleName());

      context.registerBeanDefinition(beanName, beanDefinition);
      log.info("Registered HTTP client bean: {}", beanName);
    }
  }

  private <T> T createClient(Class<T> clientClass) {
    var baseUrl = resolveBaseUrl(clientClass);
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builder()
            .exchangeAdapter(baseUrl.map(this::client).orElseGet(this::client))
            .build();

    return factory.createClient(clientClass);
  }

  private Optional<String> resolveBaseUrl(Class<?> clientClass) {
    var annotation = clientClass.getAnnotation(BaseUrl.class);
    if (annotation != null) {
      String value = annotation.value();
      if (value.contains("${")) {
        return Optional.of(environment.resolvePlaceholders(value));
      }
      return Optional.of(value);
    }

    return Optional.empty();
  }

  private HttpExchangeAdapter client(String baseUrl) {
    var client =
        WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector())
            .build();

    return WebClientAdapter.create(client);
  }

  private HttpExchangeAdapter client() {
    var client = WebClient.builder().clientConnector(new ReactorClientHttpConnector()).build();

    return WebClientAdapter.create(client);
  }

  private String standardizeBeanName(String className) {
    return className.substring(0, 1).toLowerCase() + className.substring(1);
  }
}
