package com.hixtrip.sample.infra.handle;

public interface PaymentStrategy {

    String payStatus();

    void callback(String orderId);
}
