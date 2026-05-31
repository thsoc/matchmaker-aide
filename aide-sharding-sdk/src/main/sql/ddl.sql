-- 创建桶映射配置表
CREATE TABLE IF NOT EXISTS `bucket_mapping` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `bucket_start` INT NOT NULL COMMENT '桶起始编号',
  `bucket_end` INT NOT NULL COMMENT '桶结束编号',
  `data_source` VARCHAR(20) NOT NULL COMMENT '数据源名称',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_bucket_range` (`bucket_start`, `bucket_end`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='哈希桶映射配置表';

-- 初始化默认配置（2个数据库）
INSERT INTO `bucket_mapping` (`bucket_start`, `bucket_end`, `data_source`, `status`) VALUES
(0, 127, 'ds0', 1),
(128, 255, 'ds1', 1);

-- 扩容时添加新配置（3个数据库）
-- UPDATE bucket_mapping SET data_source = 'ds0' WHERE bucket_start = 0 AND bucket_end = 63;
-- UPDATE bucket_mapping SET data_source = 'ds1' WHERE bucket_start = 64 AND bucket_end = 127;
-- INSERT INTO bucket_mapping (bucket_start, bucket_end, data_source, status) VALUES (128, 191, 'ds2', 1);
-- INSERT INTO bucket_mapping (bucket_start, bucket_end, data_source, status) VALUES (192, 255, 'ds3', 1);
