-- ============================================
-- 积分表 - aide_points
-- ID生成策略：雪花算法（MyBatis-Plus ASSIGN_ID）
-- ============================================

DROP TABLE IF EXISTS `aide_points`;

CREATE TABLE `aide_points` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法生成）',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `points_type` TINYINT NOT NULL COMMENT '积分类型',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `points` INT NOT NULL DEFAULT 0 COMMENT '积分数量',
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
  UNIQUE KEY `idx_orderNo_time` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分表';
