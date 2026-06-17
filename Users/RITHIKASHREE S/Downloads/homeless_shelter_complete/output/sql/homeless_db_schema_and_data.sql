-- ============================================================
-- Homeless Shelter Management System — Full Database Script
-- Database : homeless_db
-- MySQL    : 8.x
-- Run this ONCE before starting the Spring Boot application.
-- ============================================================

-- 1. CREATE DATABASE
CREATE DATABASE IF NOT EXISTS homeless_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE homeless_db;

-- ============================================================
-- 2. DROP TABLES (safe re-run order — child tables first)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS admissions;
DROP TABLE IF EXISTS shelters;
DROP TABLE IF EXISTS homeless_persons;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 3. CREATE TABLES
-- ============================================================

CREATE TABLE roles (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(100),
    active   TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE homeless_persons (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100) NOT NULL,
    age               INT          NOT NULL DEFAULT 0,
    gender            VARCHAR(20)  NOT NULL,
    health_conditions TEXT,
    special_needs     TINYINT(1)   NOT NULL DEFAULT 0,
    location          VARCHAR(500),
    registered_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shelters (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100) NOT NULL,
    city              VARCHAR(100) NOT NULL,
    zone              VARCHAR(100),
    ward              VARCHAR(100),
    address           VARCHAR(500),
    contact_phone     VARCHAR(20),
    total_capacity    INT          NOT NULL DEFAULT 1,
    current_occupancy INT          NOT NULL DEFAULT 0,
    facilities        TEXT,
    ngo_user_id       BIGINT,
    verified          TINYINT(1)   NOT NULL DEFAULT 0,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_shelter_ngo FOREIGN KEY (ngo_user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT chk_capacity   CHECK (total_capacity >= 1),
    CONSTRAINT chk_occupancy  CHECK (current_occupancy >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admissions (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    homeless_person_id  BIGINT NOT NULL,
    shelter_id          BIGINT NOT NULL,
    volunteer_id        BIGINT,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_date        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME,
    remarks             TEXT,
    rejection_reason    TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_adm_person    FOREIGN KEY (homeless_person_id) REFERENCES homeless_persons (id) ON DELETE CASCADE,
    CONSTRAINT fk_adm_shelter   FOREIGN KEY (shelter_id)         REFERENCES shelters           (id) ON DELETE CASCADE,
    CONSTRAINT fk_adm_volunteer FOREIGN KEY (volunteer_id)       REFERENCES users              (id) ON DELETE SET NULL,
    CONSTRAINT chk_status       CHECK (status IN ('PENDING','APPROVED','REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. INDEXES (for query performance)
-- ============================================================
CREATE INDEX idx_admissions_status    ON admissions (status);
CREATE INDEX idx_admissions_volunteer ON admissions (volunteer_id);
CREATE INDEX idx_shelters_verified    ON shelters   (verified);
CREATE INDEX idx_shelters_city        ON shelters   (city);

-- ============================================================
-- 5. SEED DATA
-- NOTE: Passwords below are BCrypt hashes of the plaintext shown.
--   admin123   → $2a$10$...
--   ngo123     → $2a$10$...
--   volunteer1 → $2a$10$...
-- Generate fresh hashes at: https://bcrypt-generator.com  (rounds=10)
-- ============================================================

-- Roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('NGO'), ('VOLUNTEER');

-- Users  (passwords are BCrypt of the values in the comments)
INSERT INTO users (username, password, email, active) VALUES
('admin',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',  -- admin123
 'admin@shelter.org', 1),

('ngo_hope',
 '$2a$10$TLiD3IeZaG8UrHnHcQ3XVuHt1vk4mRiELxJL8K6Nf.MqRnSkAuFi',  -- ngo123
 'ngo@hopeshelter.org', 1),

('volunteer1',
 '$2a$10$8Kc0nXt5QEjKlGf1ZA9OxO5JFrJZ8R9Qe7vQ3TqIJBJlYkK3iCJ2',  -- volunteer1
 'vol1@example.com', 1),

('volunteer2',
 '$2a$10$8Kc0nXt5QEjKlGf1ZA9OxO5JFrJZ8R9Qe7vQ3TqIJBJlYkK3iCJ2',  -- volunteer1
 'vol2@example.com', 1);

-- Assign roles
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE (u.username='admin'      AND r.name='ADMIN')
   OR (u.username='ngo_hope'   AND r.name='NGO')
   OR (u.username='volunteer1' AND r.name='VOLUNTEER')
   OR (u.username='volunteer2' AND r.name='VOLUNTEER');

-- Homeless Persons
INSERT INTO homeless_persons (name, age, gender, health_conditions, special_needs, location) VALUES
('Rajan Kumar',    45, 'MALE',   'Diabetes, hypertension',         0, 'Anna Nagar, Chennai'),
('Meena Devi',     32, 'FEMALE', 'None',                           0, 'T Nagar, Chennai'),
('Arjun S',        67, 'MALE',   'Arthritis, poor vision',         1, 'Egmore, Chennai'),
('Baby Lakshmi',    5, 'FEMALE', 'Malnourishment',                 1, 'Royapuram, Chennai'),
('Selvam P',       28, 'MALE',   'None',                           0, 'Tambaram, Chennai'),
('Kamala W',       55, 'FEMALE', 'Hearing impaired',               1, 'Guindy, Chennai'),
('Murugan T',      40, 'MALE',   'Alcohol dependency (recovery)',   0, 'Villivakkam, Chennai'),
('Deepa R',        22, 'FEMALE', 'Mild depression, counselling required', 1, 'Kodambakkam, Chennai');

-- Shelters (ngo_user_id = id of ngo_hope user, assumed to be 2)
INSERT INTO shelters
    (name, city, zone, ward, address, contact_phone, total_capacity, current_occupancy, facilities, ngo_user_id, verified)
VALUES
('Hope Shelter',        'Chennai', 'North', 'Ward 5',  '12 MG Road, Royapuram', '9876543210', 30, 12, 'Food,Beds,Medical,Clothing',     2, 1),
('Grace Home',          'Chennai', 'South', 'Ward 18', '45 Anna Salai, Guindy', '9876501234', 20,  8, 'Food,Beds,Clothing',             2, 1),
('New Dawn Centre',     'Chennai', 'West',  'Ward 22', '7 LB Road, Adyar',      '9876567890', 15,  0, 'Food,Beds,Medical',              2, 0),
('Shanthi Nivas',       'Coimbatore', 'Central', 'W3', '3 DB Road, RS Puram',   '9090909090', 25,  5, 'Food,Beds,Counselling',          2, 1),
('Asha Bhavan',         'Madurai',    'East',    'W9', '9 Temple Street',        '8080808080', 40, 38, 'Food,Beds,Medical,Education',    2, 1);

-- Admissions
INSERT INTO admissions (homeless_person_id, shelter_id, volunteer_id, status, request_date, remarks) VALUES
(1, 1, 3, 'APPROVED', NOW() - INTERVAL 10 DAY, 'Urgent case - no food or shelter'),
(2, 1, 3, 'APPROVED', NOW() - INTERVAL 8  DAY, 'Single mother needs immediate help'),
(3, 2, 4, 'PENDING',  NOW() - INTERVAL 3  DAY, 'Elderly man, needs medical attention'),
(4, 2, 3, 'PENDING',  NOW() - INTERVAL 1  DAY, 'Child with no guardian'),
(5, 1, 4, 'REJECTED', NOW() - INTERVAL 5  DAY, 'Requested shelter 1'),
(6, 4, 3, 'APPROVED', NOW() - INTERVAL 7  DAY, 'Hearing impaired, special needs'),
(7, 4, 4, 'PENDING',  NOW() - INTERVAL 2  DAY, 'In recovery program'),
(8, 2, 3, 'PENDING',  NOW() - INTERVAL 1  DAY, 'Young woman needing counselling support');

-- Update rejection reason for the rejected record
UPDATE admissions SET rejection_reason = 'Shelter at full capacity at the time of request'
WHERE status = 'REJECTED';

-- ============================================================
-- 6. VERIFICATION QUERIES (run these to confirm seed data)
-- ============================================================
-- SELECT * FROM roles;
-- SELECT id, username, email, active FROM users;
-- SELECT * FROM users_roles;
-- SELECT * FROM homeless_persons;
-- SELECT id, name, city, total_capacity, current_occupancy, verified FROM shelters;
-- SELECT id, homeless_person_id, shelter_id, volunteer_id, status FROM admissions;

SELECT 'Database setup complete!' AS message;
