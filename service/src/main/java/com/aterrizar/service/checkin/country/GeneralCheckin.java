package com.aterrizar.service.checkin.country;

import com.aterrizar.service.checkin.flow.GeneralCheckinFlow;
import com.aterrizar.service.checkin.flow.GeneralInitFlow;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import com.aterrizar.service.core.framework.strategy.CheckinCountryStrategy;
import com.neovisionaries.i18n.CountryCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeneralCheckin extends CheckinCountryStrategy {
    private final GeneralInitFlow generalInitFlow;
    private final GeneralCheckinFlow generalCheckinFlow;

    @Override
    protected FlowStrategy getInitFlow() {
        return generalInitFlow;
    }

    @Override
    protected FlowStrategy getCheckinFlow() {
        return generalCheckinFlow;
    }

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.UNDEFINED;
    }
}
