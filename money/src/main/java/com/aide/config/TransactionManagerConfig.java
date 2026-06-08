package com.aide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/8
 * @date 18:07
 */
@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
        manager.setNestedTransactionAllowed(true);
        manager.setRollbackOnCommitFailure(true);
        return manager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        TransactionTemplate template = new TransactionTemplate(manager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setTimeout(30);
        return template;
    }
}