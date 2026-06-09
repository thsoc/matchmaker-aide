-- MoneyDo 对应表（用户账户表）
CREATE TABLE aide_money (
    id BIGINT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,  -- 每用户唯一
    available_money  DECIMAL(10,2) DEFAULT 0.00, -- 可用金额
    frozen_money  DECIMAL(10,2) DEFAULT 0.00, -- 冻结金额
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';

-- RechargeRecordDo 对应表（充值记录表）
CREATE TABLE aide_recharge_record (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,         -- 可以有多条
    amount DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL,         -- 0-待支付 1-充值中 2-成功 3-失败
    order_no VARCHAR(64) UNIQUE NOT NULL,
    pay_type TINYINT,
    payment_result VARCHAR(64),
    recharge_time DATETIME,
    create_time DATETIME,
    update_time DATETIME,
    remark VARCHAR(500),
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

-- 在money模块的DDL.sql中添加
CREATE TABLE aide_transaction_record (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-充值 2-消费 3-退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    balance_before DECIMAL(10,2) COMMENT '交易前余额',
    balance_after DECIMAL(10,2) COMMENT '交易后余额',
    related_order_no VARCHAR(64) COMMENT '关联订单号',
    business_type TINYINT COMMENT '业务类型：1-会员购买 2-其他',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-成功 2-失败',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (related_order_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

CREATE TABLE IF NOT EXISTS `tcc_fence_log`
(
    `xid`           VARCHAR(128)  NOT NULL COMMENT 'global id',
    `branch_id`     BIGINT        NOT NULL COMMENT 'branch id',
    `action_name`   VARCHAR(64)   NOT NULL COMMENT 'action name',
    `status`        TINYINT       NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)',
    `gmt_create`    DATETIME(3)   NOT NULL COMMENT 'create time',
    `gmt_modified`  DATETIME(3)   NOT NULL COMMENT 'update time',
    PRIMARY KEY (`xid`, `branch_id`),
    KEY `idx_gmt_modified` (`gmt_modified`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;



