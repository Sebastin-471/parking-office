-- Script de migración para remover los espacios y agregar vehículos de Perú

-- 1. Eliminar la llave foránea que conecta movimientos con espacios
ALTER TABLE movimientos DROP CONSTRAINT IF EXISTS fk_movimientos_espacios;

-- 2. Eliminar la columna espacio_id de movimientos
ALTER TABLE movimientos DROP COLUMN IF EXISTS espacio_id;

-- 3. Eliminar la tabla espacios
DROP TABLE IF EXISTS espacios CASCADE;

-- 4. Limpiar los tipos de vehículos existentes para insertar los nuevos
DELETE FROM tarifas; -- Para evitar problemas de llaves foráneas al eliminar tipos
DELETE FROM vehiculos; -- Para evitar problemas de llaves foráneas
DELETE FROM tipos_vehiculo;

-- 5. Insertar los tipos de vehículos más comunes en Perú
INSERT INTO tipos_vehiculo (nombre, descripcion) VALUES 
('AUTOMOVIL', 'Automovil'),
('MOTOCICLETA', 'Moto'),
('MOTOTAXI', 'Mototaxi'),
('CAMIONETA', 'Camioneta');
