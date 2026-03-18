package com.aterrizar.http.external.gateway.tax;

import org.springframework.stereotype.Service;

import com.aterrizar.service.core.model.session.TaxData;
import com.aterrizar.service.external.TaxGateway;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaxGatewayAdapter implements TaxGateway {
    private final TaxHttpClient taxHttpClient;

    @Override
    public TaxData getTax(String rfc) {
        var taxDto = taxHttpClient.getTax(rfc);
        return new TaxData(taxDto.taxAmount());
    }
}
