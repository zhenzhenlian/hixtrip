package com.hixtrip.sample.infra;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

        //更改下单时间和状态
        LambdaUpdateWrapper<OrderDO> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(OrderDO::getId, orderId);
        updateWrapper.eq(OrderDO::getPayStatus, PaymentStatusEnum.TO_BE_PAY.getValue());
        OrderDO orderDO = OrderDO.builder()
                .status(OrderStatusEnum.TO_BE_SHIP.getValue())
                .payStatus(PaymentStatusEnum.getValueByStatus(payStatus))
                .payTime(LocalDateTime.now())
                .build();
        int update = orderMapper.update(orderDO, updateWrapper);
        // 校验幂等
        if (update == 0) {
            throw new RuntimeException("当前订单状态已变更，非待支付状态");
        }
        orderDO = orderMapper.selectById(orderId);
        return OrderDOConvertor.INSTANCE.domainToDO(orderDO);
    }

    private String getOrderId() {
        // todo 使用雪花算法获取id
        return "";
    }


}
