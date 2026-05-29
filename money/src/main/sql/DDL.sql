-- MoneyDo 对应表（用户账户表）
CREATE TABLE aide_money (
    id BIGINT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,  -- 每用户唯一
    money DECIMAL(10,2) DEFAULT 0.00,
    INDEX idx_user_id (user_id)
);

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
);

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
