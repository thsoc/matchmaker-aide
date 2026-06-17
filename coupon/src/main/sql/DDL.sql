-- ============================================
-- 用户表 - aide_order
-- ID生成策略：雪花算法（MyBatis-Plus ASSIGN_ID）
-- ============================================

DROP TABLE IF EXISTS `aide_order`;

CREATE TABLE `aide_order` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法生成）',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `order_type` TINYINT NOT NULL COMMENT '订单类型',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT NOT NULL,
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
  KEY `idx_delete_time` (`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
