package com.parkingoffice.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@Testcontainers
public abstract class AbstractRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("parking_office")
            .withUsername("postgres")
            .withPassword("postgres");

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.user", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
        System.setProperty("db.pool_size", "5");
        System.setProperty("db.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("db.show_sql", "false");
        System.setProperty("db.format_sql", "false");
        System.setProperty("db.hbm2ddl.auto", "update");

        runSchema();
    }

    @AfterEach
    void tearDown() {
        HibernateUtil.shutdown();
    }

    private void runSchema() throws SQLException, IOException {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = conn.createStatement()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("database/parking_system_schema.sql")) {
                if (is != null) {
                    String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    for (String s : sql.split(";")) {
                        if (!s.trim().isEmpty()) {
                            statement.execute(s.trim());
                        }
                    }
                }
            }
        }
    }
}