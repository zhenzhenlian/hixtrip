package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.pay.model.CommandPay;
import com.hixtrip.sample.domain.pay.respository.PayRepository;
import com.hixtrip.sample.infra.handle.PaymentHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class PayRepositoryImpl implements PayRepository {

    @Autowired
    private PaymentHandle paymentHandle;


    @Override
    public void payCallBack(CommandPay commandPay) {
        paymentHandle.execute(commandPay);
    }


}
