-- Script para inicializar tarifas por defecto para todos los vehículos

-- 1. Automóvil
INSERT INTO tarifas (tipo_vehiculo_id, monto_por_hora, monto_por_minuto, activa)
SELECT id, 5.00, 0.08, true FROM tipos_vehiculo WHERE nombre = 'AUTOMOVIL'
ON CONFLICT DO NOTHING;

-- 2. Motocicleta
INSERT INTO tarifas (tipo_vehiculo_id, monto_por_hora, monto_por_minuto, activa)
SELECT id, 3.00, 0.05, true FROM tipos_vehiculo WHERE nombre = 'MOTOCICLETA'
ON CONFLICT DO NOTHING;

-- 3. Mototaxi
INSERT INTO tarifas (tipo_vehiculo_id, monto_por_hora, monto_por_minuto, activa)
SELECT id, 3.50, 0.06, true FROM tipos_vehiculo WHERE nombre = 'MOTOTAXI'
ON CONFLICT DO NOTHING;

-- 4. Camioneta
INSERT INTO tarifas (tipo_vehiculo_id, monto_por_hora, monto_por_minuto, activa)
SELECT id, 7.00, 0.12, true FROM tipos_vehiculo WHERE nombre = 'CAMIONETA'
ON CONFLICT DO NOTHING;
