package com.hixtrip.sample.domain.pay.respository;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;

/**
 *
 */
public interface PayRepository {
    void payCallBack(CommandPay commandPay);

}
