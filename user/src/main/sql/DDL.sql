-- ============================================
-- 用户表 - aide_user
-- ID生成策略：雪花算法（MyBatis-Plus ASSIGN_ID）
-- ============================================

DROP TABLE IF EXISTS `aide_user`;

CREATE TABLE `aide_user` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法生成）',
  `account` VARCHAR(50) DEFAULT NULL COMMENT '账号',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `introduce` VARCHAR(500) DEFAULT NULL COMMENT '自我介绍',
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色',
  `status` VARCHAR(10) DEFAULT 'NORMAL' COMMENT '状态',
  `sex` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `birthday` VARCHAR(20) DEFAULT NULL COMMENT '生日',
  `income` DECIMAL(10,2) DEFAULT NULL COMMENT '收入',
  `occupation` VARCHAR(50) DEFAULT NULL COMMENT '职业',
  `integral` INT DEFAULT 0 COMMENT '积分',
  `money` DECIMAL(10,2) DEFAULT 0.00 COMMENT '金额',
  `login_count` INT DEFAULT 0 COMMENT '登录次数',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除状态（0-正常 1-已删除）',
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `update_by` VARCHAR(50) DEFAULT NULL COMMENT '修改人',
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
  UNIQUE KEY `uk_account` (`account`),
  KEY `idx_username` (`username`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
