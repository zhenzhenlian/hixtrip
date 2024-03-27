#todo 你的建表语句,包含索引
-- 按照用户角色分成用户库、商家库、运营库（客服和运营查看数据统计等）
-- 对于订单的其他扩展表， 比如一个订单多个sku的需要拆分order_item、refund_item、支付需要记录支付渠道和支付订单号作为溯源依据等，这边不做展开。
-- 用户下单数据只写入用户库的订单表，商家库（允许秒级延迟）和运营库（允许分钟级延迟）可以通过mq推送或者canal等方式进行同步
-- 用户库的订单表可以进行分库分表，可以通过user_id取模作为sharding-key，查询接口需要加上redis缓存（使用用户id作为key，订单id作为list存储），有新增订单则更新缓存
-- 商家的订单查询接口可以增加缓存，缓存存储为redis，结构设计如下
-- 客服查找客诉订单的接口，
      -- 条件为订单后几位的时候，可以通过redis查询，未来如果查询条件更复杂，可以用redis-search或者 es来实现。
      -- 条件为姓名的时候，可以先通过姓名找到userid，再通过redis查询。
-- 排行榜的数据，需要写job通过对应维度统计数据到db，并且在用户下单后维护对应修改redis的结构（以下4、5的结构）
     -- 接口查询，可以通过redis的zrevrange 来获取排行榜数据。

--
-- 1.redis设计结构
--（1）订单缓存为hash：key为order:#{orderId}, 选择订单的常用字段作为订单hashkey，对应值为value。如果订单状态更新为完成后，可以给订单设置个有效期为半年;
--（2）用户订单列表的缓存： 设计为List，key使用 user:order:#{userId},使用该用户的orderId的list作为value，可以设置失效时间为7天，每次更新的时候重设失效时间;
--（3）商家订单列表的缓存： 设计为List，key使用 seller:order:#{sellerId},使用该商家的orderId的list作为value，可以设置失效时间为7天，每次更新的时候重设失效时间;
--（4）买家排行榜可以只用zset结构，买家订单数作为score，每增加一笔订单，score加1；
--（5）卖家排行榜可以只用zset结构，卖家订单数作为score，每增加一笔订单，score加1。
 -- 2.DB 设计如下
---------------------------------------- 用户库---------------------------------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
              `id` varchar(32) NOT NULL COMMENT '主键',
              `user_id` varchar(32) DEFAULT NULL COMMENT '用户',
              `user_name` varchar(32) DEFAULT NULL COMMENT '用户姓名冗余',
              `seller_id` varchar(32) DEFAULT NULL COMMENT '商家id',
              `sku_id` varchar(32) DEFAULT NULL COMMENT '商品',
              `amount` int(10) DEFAULT NULL COMMENT '数量',
              `money` decimal(10,0) DEFAULT NULL COMMENT '支付金额',
              `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
              `pay_status` tinyint(2) DEFAULT NULL COMMENT '支付状态：待付款、付款成功、付款失败',
              `status` tinyint(3) DEFAULT NULL COMMENT '订单状态: 待付款、待发货、待签收、待评价、已取消、已退款...',
              `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
              `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
              `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              `del_flag` tinyint(2) DEFAULT '0' COMMENT '删除标识，0代表未删除，1代表删除',
              PRIMARY KEY (`id`),
              KEY `index-user_id` (`user_id`) USING BTREE,
              KEY `index-order_id` (`create_time`,`id`) USING BTREE,
              KEY `index-user_name` (`create_time`,`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';


---------------------------------------- 商家库---------------------------------------------------
DROP TABLE IF EXISTS `saller_order`;
CREATE TABLE `saller_order` (
            `id` varchar(32) NOT NULL COMMENT '主键',
            `saller_id` varchar(32) DEFAULT NULL COMMENT '商家id',
            `customer_order_id` varchar(32) DEFAULT NULL COMMENT '顾客订单id',
            `customer_id` varchar(32) DEFAULT NULL COMMENT '顾客id',
            `sku_id` varchar(32) DEFAULT NULL COMMENT '商品',
            `amount` int(10) DEFAULT NULL COMMENT '数量',
            `money` decimal(10,0) DEFAULT NULL COMMENT '金额',
            `status` varchar(4) DEFAULT NULL COMMENT '订单状态',
            `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
            `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
            `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
            `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标识，0代表未删除，1代表删除',
            PRIMARY KEY (`id`),
            KEY `index-saller` (`saller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家订单';

---------------------------------------- 运营库---------------------------------------------------

DROP TABLE IF EXISTS `seller_order_statistics`;
CREATE TABLE `seller_order_statistics` (
           `id` varchar(32) NOT NULL COMMENT '主键',
           `seller_id` varchar(32) DEFAULT NULL COMMENT '商家id',
           `order_count` int(10) DEFAULT NULL COMMENT '数量',
           `order_total_amount` decimal(10,0) DEFAULT NULL COMMENT '订单总金额',
           `max_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最大金额',
           `min_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最小金额',
           `avg_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最小金额',
           `order_statistical_dimension` tinyint(4) DEFAULT NULL COMMENT '统计维度,0:日，1：月；2：年',
           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
           PRIMARY KEY (`id`),
           KEY `index-saller` (`seller_id`,`order_statistical_dimension`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家月订单汇总表';

DROP TABLE IF EXISTS `user_order_statistics`;
CREATE TABLE `user_order_statistics` (
         `id` varchar(32) NOT NULL COMMENT '主键',
         `customer_id` varchar(32) DEFAULT NULL COMMENT '顾客id',
         `order_count` int(10) DEFAULT NULL COMMENT '数量',
         `order_total_amount` decimal(10,0) DEFAULT NULL COMMENT '订单总金额',
         `max_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最大金额',
         `min_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最小金额',
         `avg_order_amount` decimal(10,0) DEFAULT NULL COMMENT '最小金额',
         `order_statistical_dimension` tinyint(4) DEFAULT NULL COMMENT '统计维度,0:日，1：月；2：年',
         `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
         PRIMARY KEY (`id`),
         KEY `index-customer` (`customer_id`,`order_statistical_dimension`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户月订单汇总表';




