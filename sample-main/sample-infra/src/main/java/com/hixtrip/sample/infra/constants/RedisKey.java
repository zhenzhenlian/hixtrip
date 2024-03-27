package com.hixtrip.sample.infra.constants;

public class RedisKey {
    private static String SKU_INVENTORY_KEY ="sku_inventory:%s";
    /**
     * 可售库存
     */
    private static String SELLABLE_QUANTITY_KEY =SKU_INVENTORY_KEY +":sellable";
    /**
     * 预占库存
     */
    private static  String WITHHOLDING_QUANTITY_KEY  =SKU_INVENTORY_KEY +":withholding";
    /**
     * 占用库存
     */
    private static String OCCUPIED_QUANTITY_KEY  =SKU_INVENTORY_KEY +":occupied";

    public static String getSellableKey(String skuId){
        return String.format(SKU_INVENTORY_KEY, skuId);
    }  public static String getWithholdingKey(String skuId){
        return String.format(SKU_INVENTORY_KEY, skuId);
    }  public static String getOccupiedKey(String skuId){
        return String.format(SKU_INVENTORY_KEY, skuId);
    }
}
