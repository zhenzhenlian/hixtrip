package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.infra.constants.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class InventoryRepositoryImpl implements InventoryRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 预占库存
     *
     * @param skuId
     * @param amount
     * @return
     */
    @Override
    public boolean withholding(String skuId, Long amount) {
        return extracted(amount,  Arrays.asList(RedisKey.getSellableKey(skuId),
                RedisKey.getWithholdingKey(skuId)));
    }

    private boolean extracted(Long amount,  List<String> keys) {
        // 构造RedisScript
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 指定要使用的lua脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/inventory_duce.lua")));
        //指定返回类型
        redisScript.setResultType(Long.class);
        // 库存预占
        Long result = redisTemplate.execute(redisScript, keys, amount);
        return result > 0;
    }

    /**
     * 占用库存并扣减:预占-n,占用+n
     *
     * @param skuId
     * @return
     */
    @Override
    public boolean occupied(String skuId, Long amount) {
        return extracted(amount,  Arrays.asList(RedisKey.getWithholdingKey(skuId),
                RedisKey.getOccupiedKey(skuId)));
    }

    /**
     * 库存回滚:预占-n,可售+n
     *
     * @param skuId
     * @return
     */
    @Override
    public boolean rollback(String skuId, Long amount) {
        return extracted(amount,  Arrays.asList(RedisKey.getWithholdingKey(skuId),
                RedisKey.getSellableKey(skuId)));
    }

}
