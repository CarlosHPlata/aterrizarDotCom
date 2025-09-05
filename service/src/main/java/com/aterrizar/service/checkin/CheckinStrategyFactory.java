package com.aterrizar.service.checkin;

import com.aterrizar.service.core.framework.strategy.CheckinCountryStrategy;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory class for managing and providing {@link CheckinCountryStrategy} instances
 * based on {@link CountryCode}.
 * <p>
 * This class implements the Factory Design Pattern, centralizing the creation and
 * retrieval of strategies for different countries.
 * </p>
 */
@Component
public class CheckinStrategyFactory {
    private final List<CheckinCountryStrategy> strategies;

    private Map<CountryCode, CheckinCountryStrategy> strategyRegistry;

    /**
     * Constructs a new {@code CheckinStrategyFactory} with the provided list of strategies.
     *
     * @param strategies the list of {@link CheckinCountryStrategy} instances to manage
     */
    public CheckinStrategyFactory(
            @Autowired
            List<CheckinCountryStrategy> strategies
    ) {
        this.strategies = strategies;
        init();
    }

    /**
     * Initializes the factory by grouping strategies by their {@link CountryCode}
     * and ensuring no duplicate country codes exist.
     *
     * @throws IllegalStateException if duplicate {@link CountryCode}s are found
     */
    public void init() {
        Map<CountryCode, List<CheckinCountryStrategy>> groupedStrategies = strategies.stream()
                .collect(Collectors.groupingBy(CheckinCountryStrategy::getCountryCode));

        List<CountryCode> duplicates = groupedStrategies.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicates.isEmpty()) {
            throw new IllegalStateException("Duplicate CountryCodes found: " + duplicates);
        }

        strategyRegistry = groupedStrategies.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
    }

    /**
     * Retrieves the {@link CheckinCountryStrategy} for the specified {@link CountryCode}.
     * If no strategy is found for the given country code, the default strategy for
     * {@link CountryCode#UNDEFINED} is returned.
     *
     * @param countryCode the {@link CountryCode} for which to retrieve the strategy
     * @return the {@link CheckinCountryStrategy} for the specified country code,
     *         or the default strategy if none is found
     */
    public CheckinCountryStrategy getService(CountryCode countryCode) {
        return strategyRegistry.getOrDefault(countryCode, strategyRegistry.get(CountryCode.UNDEFINED));
    }
}
