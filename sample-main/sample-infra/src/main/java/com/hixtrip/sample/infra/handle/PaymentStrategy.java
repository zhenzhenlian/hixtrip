package com.hixtrip.sample.infra.handle;

public interface PaymentStrategy {
    // todo import PaymentStatusEnum
    String payStatus();

    void callback(String orderId);
}
