-- Script de configuración de base de datos para Medical Appointments App
-- Ejecutar este script en MySQL

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS aplicacionCitas
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aplicacionCitas;

-- Tabla de pacientes (ya debería existir, pero la creamos por si acaso)
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de médicos
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50)
);

-- Tabla de citas médicas
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_name VARCHAR(255) NOT NULL,
    specialty VARCHAR(255),
    urgency_level INT,
    consultation_fee INT,
    duration_minutes INT,
    clinic_location_id INT,
    availability_score INT,
    is_insurance_covered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    patient_id BIGINT,
    appointment_date_time TIMESTAMP,
    patient_symptoms TEXT,
    doctor_id BIGINT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
);

-- Insertar médicos de ejemplo
INSERT INTO doctors (name, specialty, email, phone) VALUES
('Dr. Juan García', 'Medicina General', 'juan.garcia@hospital.com', '555-0101'),
('Dra. María López', 'Cardiología', 'maria.lopez@hospital.com', '555-0102'),
('Dr. Carlos Rodríguez', 'Pediatría', 'carlos.rodriguez@hospital.com', '555-0103'),
('Dra. Ana Martínez', 'Dermatología', 'ana.martinez@hospital.com', '555-0104'),
('Dr. Luis Fernández', 'Traumatología', 'luis.fernandez@hospital.com', '555-0105'),
('Dra. Isabel Torres', 'Ginecología', 'isabel.torres@hospital.com', '555-0106'),
('Dr. Miguel Sánchez', 'Oftalmología', 'miguel.sanchez@hospital.com', '555-0107'),
('Dra. Carmen Ruiz', 'Neurología', 'carmen.ruiz@hospital.com', '555-0108'),
('Dr. Francisco Díaz', 'Otorrinolaringología', 'francisco.diaz@hospital.com', '555-0109'),
('Dra. Laura Jiménez', 'Psiquiatría', 'laura.jimenez@hospital.com', '555-0110'),
('Dr. Pedro Moreno', 'Endocrinología', 'pedro.moreno@hospital.com', '555-0111'),
('Dra. Sofía Álvarez', 'Reumatología', 'sofia.alvarez@hospital.com', '555-0112');

-- Insertar paciente de prueba (contraseña: test123)
-- Nota: En producción, las contraseñas deberían estar hasheadas
INSERT INTO patients (name, email, password, phone) VALUES
('Paciente Prueba', 'paciente@test.com', 'test123', '555-1234');

-- Insertar algunas citas de ejemplo
INSERT INTO appointments (
    doctor_name, 
    specialty, 
    patient_id, 
    doctor_id, 
    appointment_date_time, 
    patient_symptoms,
    urgency_level,
    consultation_fee,
    duration_minutes
) VALUES
(
    'Dr. Juan García',
    'Medicina General',
    1,
    1,
    '2026-02-01 10:00:00',
    'Dolor de cabeza persistente desde hace 3 días',
    2,
    50,
    30
),
(
    'Dra. María López',
    'Cardiología',
    1,
    2,
    '2026-02-05 15:30:00',
    'Control de presión arterial',
    1,
    80,
    45
);

SELECT 'Base de datos configurada correctamente' AS mensaje;
