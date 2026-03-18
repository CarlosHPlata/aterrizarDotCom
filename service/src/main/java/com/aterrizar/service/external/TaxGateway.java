package com.aterrizar.service.external;

import com.aterrizar.service.core.model.session.TaxData;

public interface TaxGateway {
    TaxData getTax(String rfc);
}
