-- 会员表
CREATE TABLE aide_member (
    id BIGINT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    member_type TINYINT NOT NULL COMMENT '会员类型：1-普通会员 2-高级会员 3-VIP会员',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '会员状态：0-未激活 1-已激活 2-已过期',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';
