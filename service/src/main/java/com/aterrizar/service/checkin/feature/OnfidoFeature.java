package com.aterrizar.service.checkin.feature;

public interface OnfidoFeature {

    /**
     * Returns true if the given country code should use Onfido (high-security provider).
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @return true when Onfido must be used for this country
     */
    boolean isCountryAvailable(String countryCode);
}
