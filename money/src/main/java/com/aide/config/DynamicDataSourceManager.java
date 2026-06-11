package com.aide.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mazg
 * @description 数据源管理器：暂不使用
 * @date 2026/6/10
 * @date 18:53
 */
//@Component
//@EnableTransactionManagement
@Slf4j
public class DynamicDataSourceManager implements DisposableBean {
    @Autowired
    private DynamicDataSourceRegistry registry;

    // 数据源实例缓存
    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
    private final Map<String, JdbcTemplate> jdbcTemplateCache = new ConcurrentHashMap<>();

    // 使用读写锁优化并发性能
    private final Map<String, ReentrantLock> creationLocks = new ConcurrentHashMap<>();

    // 使用AtomicReference保证可见性
    private final AtomicReference<Boolean> initialized = new AtomicReference<>(false);

    /**
     * 初始化数据源
     */
    @PostConstruct
    public void init() {
        // 初始化所有分片的数据源
        for (String shardId : registry.getActiveShards()) {
            createHikariDataSource(shardId);
        }

        registry.getActiveShards().stream().forEach(a->
                new Thread(()-> healthCheck(a)).start());
    }


    /**
     * 创建HikariCP数据源（线程安全）
     */
    private HikariDataSource createHikariDataSource(String shardKey) {
        // 从注册中心获取配置
        DynamicDataSourceRegistry.ShardConfig config = registry.getShardConfig(shardKey);
        if (config == null) {
            throw new IllegalArgumentException("分片配置不存在: " + shardKey);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());

        // 设置连接池参数
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("HikariPool-" + shardKey);

        // 重要：设置数据源属性
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        dataSourceCache.put(shardKey, dataSource);
        jdbcTemplateCache.put(shardKey, new JdbcTemplate(dataSource));
        return dataSource;
    }


    /**
     * 根据分片键获取数据源
     */
    public DataSource getDataSourceByKey(String routingKey) {
        // 1. 快速路径：直接从缓存获取
        DataSource dataSource = dataSourceCache.get(routingKey);
        if (dataSource != null) {
            return dataSource;
        }
        // 2. 慢速路径：创建数据源
        return getOrCreateDataSource(routingKey);
    }

    /**
     * 安全的创建或获取数据源
     */
    private DataSource getOrCreateDataSource(String shardKey) {
        // 使用分段锁，避免对整个缓存加锁
        ReentrantLock lock = creationLocks.computeIfAbsent(shardKey, k -> new ReentrantLock());

        lock.lock();
        try {
            // 双重检查
            DataSource dataSource = dataSourceCache.get(shardKey);
            if (dataSource != null) {
                return dataSource;
            }

            // 创建新的数据源
            dataSource = createHikariDataSource(shardKey);
            DataSource previous = dataSourceCache.putIfAbsent(shardKey, dataSource);

            // 确保只有一个数据源实例
            if (previous != null) {
                // 如果已经存在，关闭新创建的数据源
                closeDataSourceSilently((HikariDataSource) dataSource);
                return previous;
            }

            log.info("创建数据源成功: {}", shardKey);
            return dataSource;

        } finally {
            lock.unlock();
            // 清理锁对象，避免内存泄漏
            creationLocks.remove(shardKey);
        }
    }

    /**
     * 根据分片ID获取数据源
     */
    public DataSource getDataSourceById(String shardId) {
        DataSource dataSource = dataSourceCache.get(shardId);
        if (dataSource == null) {
            throw new IllegalArgumentException("分片不存在: " + shardId);
        }
        return dataSource;
    }

    /**
     * 根据分片键获取JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplateByKey(String routingKey) {
        String shardId = registry.getShardForKey(routingKey);
        return jdbcTemplateCache.get(shardId);
    }

    /**
     * 根据分片ID获取JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplateById(String shardId) {
        JdbcTemplate jdbcTemplate = jdbcTemplateCache.get(shardId);
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("分片不存在: " + shardId);
        }
        return jdbcTemplate;
    }

    /**
     * 动态添加分片
     */
    public synchronized void addShard(DynamicDataSourceRegistry.ShardConfig config) {
        registry.addShard(config);
        createHikariDataSource(config.getId());
    }

    /**
     * 安全的移除数据源
     */
    public void removeDataSource(String shardKey) {
        DataSource dataSource = dataSourceCache.remove(shardKey);
        if (dataSource != null && dataSource instanceof HikariDataSource) {
            closeDataSourceSilently((HikariDataSource) dataSource);
            log.info("移除数据源: {}", shardKey);
        }
    }
    /**
     * 安全关闭数据源
     */
    private void closeDataSourceSilently(HikariDataSource dataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                dataSource.close();
            } catch (Exception e) {
                log.warn("关闭数据源时发生异常", e);
            }
        }
    }

    /**
     * 线程安全的健康检查
     */
    public boolean healthCheck(String shardKey) {
        DataSource dataSource = dataSourceCache.get(shardKey);
        if (dataSource == null) {
            return false;
        }

        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(5); // 5秒超时
        } catch (SQLException e) {
            log.error("数据源健康检查失败: {}", shardKey, e);

            // 移除故障数据源
            removeDataSource(shardKey);
            return false;
        }
    }

    /**
     * 获取所有数据源状态
     */
    public String getDataSourceStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("数据源状态：\n");

        for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
            String shardId = entry.getKey();
            HikariDataSource ds = (HikariDataSource)entry.getValue();

            sb.append("分片: ").append(shardId)
                    .append(", 活跃连接: ").append(ds.getHikariPoolMXBean().getActiveConnections())
                    .append(", 空闲连接: ").append(ds.getHikariPoolMXBean().getIdleConnections())
                    .append(", 总连接: ").append(ds.getHikariPoolMXBean().getTotalConnections())
                    .append("\n");
        }

        return sb.toString();
    }

    /**
     * 清理所有数据源
     */
    @Override
    public void destroy() throws Exception {
        log.info("开始清理所有数据源...");

        for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
            String shardKey = entry.getKey();
            DataSource dataSource = entry.getValue();

            if (dataSource instanceof HikariDataSource) {
                closeDataSourceSilently((HikariDataSource) dataSource);
                log.debug("已关闭数据源: {}", shardKey);
            }
        }

        dataSourceCache.clear();
        creationLocks.clear();
        log.info("所有数据源已清理完成");
    }

    /**
     * 获取缓存大小
     */
    public int getCacheSize() {
        return dataSourceCache.size();
    }
}
