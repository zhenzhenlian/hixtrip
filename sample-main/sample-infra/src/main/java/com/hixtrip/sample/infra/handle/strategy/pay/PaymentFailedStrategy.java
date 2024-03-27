package com.hixtrip.sample.infra.handle.strategy.pay;

import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.infra.enums.PaymentStatusEnum;
import com.hixtrip.sample.infra.handle.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentFailedStrategy implements PaymentStrategy {
    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Override
    public String payStatus() {
        return PaymentStatusEnum.FAILED.getStatus();
    }

    @Override
    public void callback(String orderId) {
        log.error("支付失败");
        Order order = orderDomainService.orderPayFail(orderId, payStatus());
        //释放库存，简单认为支付失败则订单结束，如果是支付失败有半小时等待期，则不释放库存
        inventoryDomainService.changeInventory(order.getSkuId(), Long.valueOf(order.getAmount()), null, null);

    }
}
