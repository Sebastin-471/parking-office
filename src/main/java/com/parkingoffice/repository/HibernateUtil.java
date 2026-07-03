package com.parkingoffice.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.parkingoffice.core.ConfigLoader;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url", ConfigLoader.getProperty("db.url"))
                .applySetting("hibernate.connection.username", ConfigLoader.getProperty("db.user"))
                .applySetting("hibernate.connection.password", ConfigLoader.getProperty("db.password"))
                .applySetting("hibernate.connection.pool_size", ConfigLoader.getProperty("db.pool_size"))
                .applySetting("hibernate.dialect", ConfigLoader.getProperty("db.dialect"))
                .applySetting("hibernate.show_sql", ConfigLoader.getProperty("db.show_sql"))
                .applySetting("hibernate.format_sql", ConfigLoader.getProperty("db.format_sql"))
                .applySetting("hibernate.hbm2ddl.auto", ConfigLoader.getProperty("db.hbm2ddl.auto"))
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
