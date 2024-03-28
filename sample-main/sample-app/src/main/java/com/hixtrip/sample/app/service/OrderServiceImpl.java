package com.hixtrip.sample.app.service;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.convertor.OrderConvertor;
import com.hixtrip.sample.app.convertor.PayConvertor;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.commodity.CommodityDomainService;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.PayDomainService;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * app层负责处理request请求，调用领域服务
 */
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private InventoryDomainService inventoryDomainService;
    @Autowired
    private PayDomainService payDomainService;

    @Autowired
    private CommodityDomainService commodityDomainService;

    @Override
    public String creatOrder(Order request) {
        // todo 扣减可售库存
        Boolean result = inventoryDomainService.changeInventory(request.getSkuId(), null, request.getAmount().longValue(), null);
        if (!result) {
            throw new RuntimeException("库存不足");
        }
        BigDecimal skuPrice = commodityDomainService.getSkuPrice(request.getSkuId());
        request.setMoney(skuPrice.multiply(BigDecimal.valueOf(request.getAmount())));
        try {
            Order order = orderDomainService.createOrder(request);
            return order.getId();
        } catch (Exception e) {
            // 失败需要回滚预占库存，可售库存
            inventoryDomainService.changeInventory(request.getSkuId(), request.getAmount().longValue(), null, null);
            throw e;
        }
    }

    @Override
    public void payCallback(CommandPayDTO commandPayDTO) {
        CommandPay commandPay = PayConvertor.INSTANCE.payDTOToPay(commandPayDTO);
        payDomainService.payRecord(commandPay);
    }
}
