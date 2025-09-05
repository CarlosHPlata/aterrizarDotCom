package com.aterrizar.service;

import com.aterrizar.service.checkin.CheckinStrategyFactory;
import com.aterrizar.service.core.model.Context;
import com.aterrizar.service.core.model.InitContext;
import com.neovisionaries.i18n.CountryCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckinService {
    private final CheckinStrategyFactory checkinStrategyFactory;

    public InitContext init(Context context, CountryCode countryCode) {
        var strategy = checkinStrategyFactory.getService(countryCode);
        return strategy.init(context);
    }

    public Context checkin(Context context, CountryCode countryCode) {
        var strategy = checkinStrategyFactory.getService(countryCode);
        return strategy.checkin(context);
    }
}
