package com.hixtrip.sample.infra.handle;

import com.hixtrip.sample.domain.pay.model.CommandPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class PaymentHandle {
    @Resource
    PaymentStrategyFactory factory;

    // 支付超时，取消支付等也需要实现对应的策略
    public void execute(CommandPay commandPay) {
        PaymentStrategy paymentStrategy = factory.getStrategy(commandPay.getPayStatus());
        paymentStrategy.callback(commandPay.getOrderId());
    }

}
