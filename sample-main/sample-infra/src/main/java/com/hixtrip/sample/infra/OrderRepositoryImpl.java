package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.infra.db.convertor.OrderDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import com.hixtrip.sample.infra.enums.OrderStatusEnum;
import com.hixtrip.sample.infra.enums.PaymentStatusEnum;
import com.hixtrip.sample.infra.handle.PaymentHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PaymentHandle paymentHandle;

    @Override
    public Order create(Order order) {
        OrderDO orderDO = OrderDOConvertor.INSTANCE.doToDomain(order);
        orderDO.setId(getOrderId());
        orderDO.setPayStatus(PaymentStatusEnum.TO_BE_PAY.getValue());
        orderDO.setStatus(OrderStatusEnum.TO_BE_PAY.getValue());
        orderMapper.insert(orderDO);
        return order;
    }

    @Override
    public Order changePayStatus(String orderId, String payStatus) {
        OrderDO orderDO = orderMapper.selectById(orderId);
        if (!PaymentStatusEnum.TO_BE_PAY.getStatus().equals(orderDO.getPayStatus())) {
            throw new RuntimeException("当前订单状态已变更，非待支付状态");
        }
        //更改下单时间和状态
        orderDO.setPayTime(LocalDateTime.now());
        orderDO.setPayStatus(PaymentStatusEnum.getValueByStatus(payStatus));
        orderMapper.updateById(orderDO);

        return OrderDOConvertor.INSTANCE.domainToDO(orderDO);
    }

    private String getOrderId(){
        // todo 使用雪花算法获取id
        return "";
    }


}
