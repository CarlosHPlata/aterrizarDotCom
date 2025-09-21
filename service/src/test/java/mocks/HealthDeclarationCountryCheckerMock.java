package mocks;

import java.util.List;

import com.aterrizar.service.checkin.feature.HealthDeclarationCountryChecker;
import com.neovisionaries.i18n.CountryCode;

public class HealthDeclarationCountryCheckerMock implements HealthDeclarationCountryChecker {
    private final List<CountryCode> countries;

    public HealthDeclarationCountryCheckerMock(CountryCode... countries) {
        this(List.of(countries));
    }

    public HealthDeclarationCountryCheckerMock(List<CountryCode> countries) {
        this.countries = countries;
    }

    public boolean isCountryEnabled(CountryCode countryCode) {
        return this.countries.contains(countryCode);
    }
}
