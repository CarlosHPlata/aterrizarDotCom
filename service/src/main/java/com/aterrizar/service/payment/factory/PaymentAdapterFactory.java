package com.aterrizar.service.payment.factory;

import com.aterrizar.service.payment.adapter.PaymentAdapter;

public interface PaymentAdapterFactory {
    PaymentAdapter getAdapter(String paymentMethod);
}
