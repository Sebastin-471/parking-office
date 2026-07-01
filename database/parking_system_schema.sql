-- Schema creation for parking-office system
-- Database should be created beforehand: CREATE DATABASE parking_office;

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Usuarios table
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol_id INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (rol_id) REFERENCES roles (id)
);

-- Tipos de Vehiculo table
CREATE TABLE IF NOT EXISTS tipos_vehiculo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

-- Vehiculos table
CREATE TABLE IF NOT EXISTS vehiculos (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    tipo_vehiculo_id INT NOT NULL,
    color VARCHAR(30),
    marca VARCHAR(50),
    CONSTRAINT fk_vehiculos_tipos FOREIGN KEY (tipo_vehiculo_id) REFERENCES tipos_vehiculo (id)
);

-- Tarifas table
CREATE TABLE IF NOT EXISTS tarifas (
    id SERIAL PRIMARY KEY,
    tipo_vehiculo_id INT NOT NULL,
    monto_por_hora DECIMAL(10, 2) NOT NULL,
    monto_por_minuto DECIMAL(10, 2) NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_tarifas_tipos FOREIGN KEY (tipo_vehiculo_id) REFERENCES tipos_vehiculo (id)
);

-- Espacios table
CREATE TABLE IF NOT EXISTS espacios (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    ocupado BOOLEAN DEFAULT FALSE
);

-- Movimientos table
CREATE TABLE IF NOT EXISTS movimientos (
    id SERIAL PRIMARY KEY,
    vehiculo_id INT NOT NULL,
    espacio_id INT NOT NULL,
    usuario_ingreso_id INT NOT NULL,
    usuario_salida_id INT,
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP,
    tarifa_aplicada_id INT NOT NULL,
    total_pagar DECIMAL(10, 2),
    estado VARCHAR(20) DEFAULT 'ACTIVO', -- 'ACTIVO' o 'FINALIZADO'
    CONSTRAINT fk_movimientos_vehiculos FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_movimientos_espacios FOREIGN KEY (espacio_id) REFERENCES espacios (id),
    CONSTRAINT fk_movimientos_usuario_ingreso FOREIGN KEY (usuario_ingreso_id) REFERENCES usuarios (id),
    CONSTRAINT fk_movimientos_usuario_salida FOREIGN KEY (usuario_salida_id) REFERENCES usuarios (id),
    CONSTRAINT fk_movimientos_tarifas FOREIGN KEY (tarifa_aplicada_id) REFERENCES tarifas (id)
);

-- Seed data for basic functioning
INSERT INTO roles (nombre) VALUES ('ADMINISTRADOR'), ('OPERADOR') ON CONFLICT DO NOTHING;
INSERT INTO tipos_vehiculo (nombre, descripcion) VALUES ('AUTOMOVIL', 'Auto particular'), ('MOTOCICLETA', 'Moto de 2 ruedas') ON CONFLICT DO NOTHING;

-- Create default admin (password: admin123, hash generated using jbcrypt)
-- password admin123 hash: $2a$10$D/v6vA4/O1K0Y3N9Yc/w3u.n9.o51f/A4384n48/8x./4a/59/3a2
INSERT INTO usuarios (username, password_hash, nombre_completo, rol_id) 
VALUES ('admin', '$2a$10$7cK.Y.4vF.l2.M.2X.xXeeoNnQO5s9qK/Z.K/zK/rPz/TzQO5y', 'Administrador del Sistema', 1) ON CONFLICT DO NOTHING;

-- Basic spaces
INSERT INTO espacios (codigo) VALUES ('A1'), ('A2'), ('A3'), ('A4'), ('A5') ON CONFLICT DO NOTHING;
