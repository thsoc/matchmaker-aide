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