package com.hixtrip.sample.infra.handle;

import com.hixtrip.sample.infra.enums.PaymentStatusEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 支付回调处理策略的工厂类
 */
@Component
public class PaymentStrategyFactory {
    @Resource
    private PaymentStrategy[] paymentStrategies;
    private final Map<String, PaymentStrategy> paymentStrategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (PaymentStrategy strategy : paymentStrategies) {
            paymentStrategyMap.put(strategy.payStatus(), strategy);
        }
    }

    public PaymentStrategy getStrategy(String payStatus) {
        return Optional.ofNullable(paymentStrategyMap.get(payStatus)).orElseThrow(() -> new RuntimeException("找不到支付回调策略"));
    }
}
