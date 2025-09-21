package com.aterrizar.service.checkin.country;

import org.springframework.stereotype.Service;

import com.aterrizar.service.checkin.flow.GeneralInitFlow;
import com.aterrizar.service.checkin.flow.VeContinueFlow;
import com.aterrizar.service.core.framework.flow.FlowStrategy;
import com.aterrizar.service.core.framework.strategy.CheckinCountryStrategy;
import com.neovisionaries.i18n.CountryCode;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VeCheckin extends CheckinCountryStrategy {

    private final GeneralInitFlow generalInitFlow;
    private final VeContinueFlow veContinueFlow;

    @Override
    protected FlowStrategy getInitFlow() {
        return generalInitFlow;
    }

    @Override
    protected FlowStrategy getContinueFlow() {
        return veContinueFlow;
    }

    @Override
    public CountryCode getCountryCode() {
        return CountryCode.VE;
    }
}
