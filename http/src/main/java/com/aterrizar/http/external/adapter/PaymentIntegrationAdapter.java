package com.aterrizar.http.external.adapter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.aterrizar.http.external.gateway.PaymentClient;
import com.aterrizar.http.external.gateway.PaymentStatusClient;
import com.aterrizar.http.external.gateway.payment.port.ExternalPaymentPort;
import com.aterrizar.service.core.model.PaymentRequestDto;

@Component
public class PaymentIntegrationAdapter implements ExternalPaymentPort {

    private final PaymentClient paymentClient;
    private final PaymentStatusClient statusClient;

    public PaymentIntegrationAdapter(
            PaymentClient paymentClient, PaymentStatusClient statusClient) {
        this.paymentClient = paymentClient;
        this.statusClient = statusClient;
    }

    @Override
    public String get3dsToken(PaymentRequestDto request) {
        Map<String, String> response =
                paymentClient.get3dsToken(Map.of("cardNumber", request.cardNumber()));
        return response.get("paymentToken");
    }

    @Override
    public String getWireToken(PaymentRequestDto request) {
        Map<String, String> response =
                paymentClient.getWireToken(Map.of("linkIdentifier", request.linkIdentifier()));
        return response.get("paymentToken");
    }

    @Override
    public String getGovToken(PaymentRequestDto request) {
        Map<String, String> response =
                paymentClient.getGovToken(Map.of("curpNumber", request.curpNumber()));
        return response.get("paymentToken");
    }

    @Override
    public String getPaymentStatus(String paymentToken) {
        // Implementation of Mock Behavior: GET /status/{token} always returns {"status": "SUCCESS"}
        Map<String, String> response = statusClient.getPaymentStatus(paymentToken);
        return response != null ? response.get("status") : "PENDING";
    }
}
