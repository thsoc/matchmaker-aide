-- ============================================
-- 优惠券模块表结构
-- ID生成策略：雪花算法（MyBatis-Plus ASSIGN_ID）
-- ============================================

DROP TABLE IF EXISTS `aide_coupon`;

CREATE TABLE `aide_coupon` (
  `id` bigint NOT NULL COMMENT '优惠券ID',
  `coupon_name` varchar(255) DEFAULT NULL COMMENT '优惠券名称',
  `effective_time` datetime DEFAULT NULL COMMENT '优惠券生效时间',
  `expire_time` datetime DEFAULT NULL COMMENT '优惠券失效时间',
  `coupon_discount_type` int DEFAULT NULL COMMENT '优惠券折扣方式 0-折扣券 1-满减券 2-代金券',
  `total_count` int DEFAULT NULL COMMENT '发行总量',
  `available_stock` int DEFAULT NULL COMMENT '剩余库存',
  `amount` decimal(10, 2) DEFAULT NULL COMMENT '对于代金券，直接存储固定抵扣金额（如 20 元）；对于折扣券，存储折扣比例（如 0.85 代表 85 折）',
  `condition_amount` decimal(10, 2) DEFAULT NULL COMMENT '使用门槛。满减券和折扣券需要填写（如满 100 可用），代金券如果无门槛则填 0。',
  `max_discount` decimal(10, 2) DEFAULT NULL COMMENT '折扣上限',
  `rule_json` text COMMENT '规则json,用于存储更复杂的扩展规则,暂时不用',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT NOT NULL COMMENT '0-创建 1-已抢光 2-已删除',
  `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '取消时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `update_by` VARCHAR(50) DEFAULT NULL COMMENT '修改人',
  `remark` VARCHAR(500) COMMENT '备注',
  `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
  `reserved1` VARCHAR(100) DEFAULT NULL COMMENT '预留字段1',
  `reserved2` VARCHAR(100) DEFAULT NULL COMMENT '预留字段2',
  `reserved3` VARCHAR(100) DEFAULT NULL COMMENT '预留字段3',
  `reserved4` VARCHAR(100) DEFAULT NULL COMMENT '预留字段4',
  `reserved5` INT DEFAULT NULL COMMENT '预留字段5',
  `reserved6` INT DEFAULT NULL COMMENT '预留字段6',
  `reserved7` INT DEFAULT NULL COMMENT '预留字段7',
  `reserved8` INT DEFAULT NULL COMMENT '预留字段8',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_effective_time` (`effective_time`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';
