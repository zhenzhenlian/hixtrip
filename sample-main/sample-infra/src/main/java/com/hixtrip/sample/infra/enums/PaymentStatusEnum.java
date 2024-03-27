package com.hixtrip.sample.infra.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatusEnum {

    TO_BE_PAY("toBePay", 0, "待支付"),
    SUCCESS("success", 1, "支付成功"),
    FAILED("failed", 2, "支付失败"),
    REPEATED("repeated", 3, "重复支付"),
    ;
    private String status;
    private Integer value;
    private String desc;

    PaymentStatusEnum(String status, Integer value, String desc) {
        this.status = status;
        this.value = value;
        this.desc = desc;

    }

    public static Integer getValueByStatus(String status) {
        Arrays.stream(PaymentStatusEnum.values()).map(paymentStatusEnum -> {
            if (paymentStatusEnum.status.equals(status)) {
                return paymentStatusEnum.value;
            }
            return null;
        });
        return null;
    }
}
