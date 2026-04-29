-- TeacherTransfer Database Initialization Script

-- 1. Create Tables (to ensure they exist for the following inserts)
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

-- 2. Insert Bihar Districts
INSERT INTO district (id, name, code, lat, lng) VALUES
(1, 'Patna', 'PAT', 25.5941, 85.1376),
(2, 'Gaya', 'GAY', 24.7955, 85.0002),
(3, 'Muzaffarpur', 'MUZ', 26.1225, 85.3906),
(4, 'Bhagalpur', 'BHA', 25.2425, 86.9842),
(5, 'Araria', 'ARA', 26.1497, 87.5146)
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Blocks for testing (at least 2 for Patna to allow match)
INSERT INTO block (id, district_id, name, code, lat, lng) VALUES
(101, 1, 'Phulwari Sharif', 'PSH', 25.5780, 85.0719),
(102, 1, 'Sampatchak', 'SAM', 25.5458, 85.1614),
(103, 1, 'Bihta', 'BIH', 25.5617, 84.8732),
(201, 2, 'Bodhgaya', 'BOD', 24.6961, 84.9912),
(202, 2, 'Sherghati', 'SHE', 24.5779, 84.7892)
ON CONFLICT (id) DO NOTHING;

-- 4. Insert Subscription Plans
INSERT INTO subscription_plan (code, name, price_paise, duration_days, features) VALUES
('BASIC_1M', 'Basic Monthly', 3900, 30, '{"matches_per_day": 10, "map_view": false}'),
('PREMIUM_1M', 'Premium Monthly', 9900, 30, '{"matches_per_day": 50, "map_view": true}'),
('PREMIUM_3M', 'Premium Quarterly', 24900, 90, '{"matches_per_day": 50, "map_view": true}')
ON CONFLICT (code) DO NOTHING;

-- Ensure sequences are restarted after manual ID inserts
SELECT setval('district_id_seq', (SELECT MAX(id) FROM district));
SELECT setval('block_id_seq', (SELECT MAX(id) FROM block));
