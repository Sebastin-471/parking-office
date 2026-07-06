package com.parkingoffice.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class ConfigLoader {
    private static final Properties properties = new Properties();

     static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                LoggerFactory.getLogger(ConfigLoader.class).warn("Lo siento, no se pudo encontrar el archivo application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(ConfigLoader.class).error("Error al cargar application.properties.", ex);
        }
    }

    public static String getProperty(String key) {
        // Permitir que las variables de entorno del sistema sobreescriban (útil para Docker/CI)
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return properties.getProperty(key);
    }
}
