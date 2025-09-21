package com.aterrizar.service.core.model.session;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record ExperimentalData(List<String> experiments) implements Serializable {
    public boolean isExperimentActive(String experimentName) {
        return experiments != null && experiments.contains(experimentName);
    }
}
