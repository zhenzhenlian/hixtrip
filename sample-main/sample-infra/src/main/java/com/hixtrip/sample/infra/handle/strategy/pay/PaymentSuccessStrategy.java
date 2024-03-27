package com.hixtrip.sample.infra.handle.strategy.pay;

import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.infra.enums.PaymentStatusEnum;
import com.hixtrip.sample.infra.handle.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentSuccessStrategy implements PaymentStrategy {
    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Override
    public String payStatus() {
        return PaymentStatusEnum.SUCCESS.getStatus();
    }

    @Override
    public void callback(String orderId) {
        log.error("支付成功");
        //占用库存
        Order order = orderDomainService.orderPaySuccess(orderId, payStatus());
        inventoryDomainService.changeInventory(order.getSkuId(), null, null, Long.valueOf(order.getAmount()));
    }
}
