package com.hixtrip.sample.domain.inventory.repository;

/**
 *
 */
public interface InventoryRepository {

    boolean addSkuInventory(String skuId, Long amount);

    /**
     * 预占库存成功才下单，预占失败则不允许下单
     * @param skuId
     * @param amount
     * @return
     */
    boolean withholding(String skuId, Long amount);

    boolean occupied(String skuId, Long amount);

    boolean rollback(String skuId, Long amount);
}
