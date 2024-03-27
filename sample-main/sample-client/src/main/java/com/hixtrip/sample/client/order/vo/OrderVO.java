package com.hixtrip.sample.client.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建订单的出参
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {

    /**
     * 商品规格id
     */
    private String skuId;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 用户id
     */
    private String userId;


}
