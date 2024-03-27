package com.hixtrip.sample.infra.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    TO_BE_PAY("toBePay",    0,"待支付"),
    TO_BE_SHIP("toBeShip", 1,"待发货"),
    TO_BE_RECEIVED("toBeReceived",2, "待收货"),
    // 具体根据业务来确定其他的状态...
    FINISH("finish", 3,"完成"),
    ;
    private  String status;
    private  Integer value;
    private String desc;
    OrderStatusEnum(String status, Integer value, String desc){
        this.status= status;
        this.value = value;
        this.desc= desc;

    }
}
