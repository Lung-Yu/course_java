package com.tygrus.task_list.infrastructure.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test class for DataJpaTest with Testcontainers
 * 專門用於 @DataJpaTest 的 PostgreSQL 容器基礎設定
 */
@Testcontainers
public abstract class PostgreSQLDataJpaTestBase {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // JPA 配置
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        
        // 禁用排程器
        registry.add("task.scheduler.enabled", () -> "false");
    }

    /**
     * 獲取 PostgreSQL 容器的 JDBC URL
     */
    protected String getJdbcUrl() {
        return postgres.getJdbcUrl();
    }

    /**
     * 獲取 PostgreSQL 容器的用戶名
     */
    protected String getUsername() {
        return postgres.getUsername();
    }

    /**
     * 獲取 PostgreSQL 容器的密碼
     */
    protected String getPassword() {
        return postgres.getPassword();
    }

    /**
     * 檢查容器是否運行
     */
    protected boolean isContainerRunning() {
        return postgres.isRunning();
    }
}