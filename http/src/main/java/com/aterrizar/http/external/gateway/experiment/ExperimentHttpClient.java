package com.aterrizar.http.external.gateway.experiment;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.aterrizar.http.config.BaseUrl;
import com.aterrizar.http.external.gateway.experiment.model.v1.ExperimentsDto;

import jakarta.validation.constraints.Pattern;

@BaseUrl("${http.client.experimental.platform.base.url}")
@HttpExchange(value = "v1", accept = "application/json", contentType = "application/json")
public interface ExperimentHttpClient {
    @GetExchange
    ExperimentsDto getExperiments(
            @RequestParam("mail")
                    @Pattern(
                            regexp = ".*__([A-Za-z]+)@checkin.com$",
                            message =
                                    "Email must end with '__<code>@checkin.com'. "
                                            + "Example: test__USJFKGBLHR@checkin.com")
                    String email);
}
