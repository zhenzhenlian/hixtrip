package com.hixtrip.sample.infra.handle.strategy.pay;

import com.hixtrip.sample.infra.enums.PaymentStatusEnum;
import com.hixtrip.sample.infra.handle.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentRepeatedStrategy implements PaymentStrategy {
    @Override
    public String payStatus() {
        return PaymentStatusEnum.REPEATED;
    }

    @Override
    public void callback(String orderId) {
        log.warn("重复支付,丢弃");
    }
}
