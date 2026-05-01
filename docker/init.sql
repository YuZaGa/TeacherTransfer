-- TeacherTransfer Database Bootstrap Schema
-- Data seeding is handled by resources/data.sql (Spring Boot auto-executes on startup)

-- 1. Create Tables (to ensure they exist for first-time init)
CREATE TABLE IF NOT EXISTS district (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_hindi VARCHAR(100),
    code VARCHAR(10) NOT NULL UNIQUE,
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS block (
    id SERIAL PRIMARY KEY,
    district_id INTEGER NOT NULL REFERENCES district(id),
    name VARCHAR(100) NOT NULL,
    name_hindi VARCHAR(100),
    code VARCHAR(20),
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscription_plan (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    price_paise INTEGER NOT NULL,
    duration_days INTEGER NOT NULL,
    features TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
