package com.hixtrip.sample.infra.db.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "order_info", autoResultMap = true)
@SuperBuilder(toBuilder = true)
public class OrderDO {
    /**
     * 订单号
     */
    private String id;


    /**
     * 购买人
     */
    private String userId;
    /**
     * 购买人
     */
    private String userName;

    /**
     * SkuId
     */
    private String skuId;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 购买金额
     */
    private BigDecimal money;

    /**
     * 购买时间
     */
    private LocalDateTime payTime;

    /**
     * 支付状态
     */
    private Integer payStatus;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Long delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}
