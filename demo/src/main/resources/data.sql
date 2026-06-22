INSERT INTO instructores (nombre, email, especialidad, anios_experiencia, activo) 
SELECT * FROM (
    SELECT 'Juan Pérez' as nombre, 'juan.perez@email.com' as email, 'Java' as especialidad, 5 as anios_experiencia, 1 as activo UNION ALL
    SELECT 'María García', 'maria.garcia@email.com', 'Spring Boot', 8, 1 UNION ALL
    SELECT 'Carlos López', 'carlos.lopez@email.com', 'Microservicios', 3, 1 UNION ALL
    SELECT 'Ana Martínez', 'ana.martinez@email.com', 'Base de Datos', 10, 0
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM instructores
) LIMIT 0;

INSERT INTO ambientes (nombre, tipo, capacidad, activo) 
SELECT * FROM (
    SELECT 'Sala 101' as nombre, 'SALA' as tipo, 30 as capacidad, 1 as activo UNION ALL
    SELECT 'Sala 102', 'SALA', 25, 1 UNION ALL
    SELECT 'Laboratorio A', 'LABORATORIO', 20, 1 UNION ALL
    SELECT 'Laboratorio B', 'LABORATORIO', 15, 1 UNION ALL
    SELECT 'Auditorio Principal', 'AUDITORIO', 100, 1 UNION ALL
    SELECT 'Sala 103', 'SALA', 35, 0
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM ambientes
) LIMIT 0;

INSERT INTO reservas (ambiente_id, instructor_id, fecha_inicio, fecha_fin, numero_aprendices, estado, fecha_creacion) 
SELECT * FROM (
    SELECT 1 as ambiente_id, 1 as instructor_id, '2024-01-15 08:00:00' as fecha_inicio, '2024-01-15 10:00:00' as fecha_fin, 20 as numero_aprendices, 'ACTIVA' as estado, NOW() as fecha_creacion UNION ALL
    SELECT 2, 1, '2024-01-15 11:00:00', '2024-01-15 13:00:00', 15, 'ACTIVA', NOW() UNION ALL
    SELECT 3, 2, '2024-01-15 09:00:00', '2024-01-15 12:00:00', 18, 'ACTIVA', NOW() UNION ALL
    SELECT 1, 1, '2024-01-15 14:00:00', '2024-01-15 16:00:00', 22, 'ACTIVA', NOW() UNION ALL
    SELECT 4, 3, '2024-01-16 08:00:00', '2024-01-16 10:00:00', 10, 'CANCELADA', NOW()
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM reservas
) LIMIT 0;