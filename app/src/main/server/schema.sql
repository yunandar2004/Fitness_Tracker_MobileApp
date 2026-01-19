--CREATE DATABASE IF NOT EXISTS fitness_database DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--USE fitness_database;
--
--CREATE TABLE IF NOT EXISTS users (
--    id INT AUTO_INCREMENT PRIMARY KEY,
--    name VARCHAR(80) NOT NULL,
--    email VARCHAR(120) NOT NULL UNIQUE,
--    password_hash VARCHAR(255) NOT NULL,
--    age INT NULL,
--    weight_kg DECIMAL(5,2) NULL,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
--CREATE TABLE IF NOT EXISTS sessions (
--    id INT AUTO_INCREMENT PRIMARY KEY,
--    user_id INT NOT NULL,
--    token CHAR(64) NOT NULL UNIQUE,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
--);
--
--CREATE TABLE IF NOT EXISTS workouts (
--    id INT AUTO_INCREMENT PRIMARY KEY,
--    user_id INT NOT NULL,
--    activity ENUM('running','cycling','weightlifting','yoga','swimming') NOT NULL,
--    duration_minutes INT NOT NULL,
--    distance_km DECIMAL(6,2) NULL,
--    calories INT NOT NULL,
--    notes TEXT NULL,
--    location VARCHAR(64) NULL,
--    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--    INDEX idx_workouts_user (user_id, recorded_at)
--);
--
--CREATE TABLE IF NOT EXISTS goals (
--    id INT AUTO_INCREMENT PRIMARY KEY,
--    user_id INT NOT NULL,
--    type ENUM('distance','duration','workouts') NOT NULL,
--    target_value DECIMAL(8,2) NOT NULL,
--    current_value DECIMAL(8,2) NOT NULL DEFAULT 0,
--    deadline DATE NOT NULL,
--    title VARCHAR(120) NOT NULL,
--    notes TEXT NULL,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
--);
--
---- TRIGGER
--DROP TRIGGER IF EXISTS trg_workout_goal_update;
--DELIMITER $$
--
--CREATE TRIGGER trg_workout_goal_update
--AFTER INSERT ON workouts
--FOR EACH ROW
--BEGIN
--    UPDATE goals
--    SET current_value =
--        IF(type = 'distance',
--            current_value + IFNULL(NEW.distance_km, 0),
--        IF(type = 'duration',
--            current_value + NEW.duration_minutes,
--        IF(type = 'workouts',
--            current_value + 1,
--            current_value)))
--    WHERE user_id = NEW.user_id;
--END$$
--
--DELIMITER ;
/* ===============================
   DATABASE
================================ */
DROP DATABASE IF EXISTS fitness_database;
CREATE DATABASE fitness_database
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE fitness_database;

/* ===============================
   USERS
================================ */
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    date_of_birth DATE NOT NULL,
    current_weight_kg DECIMAL(5,2) NULL,
    gender ENUM('male','female','other') NULL,

    role ENUM('user','admin') NOT NULL DEFAULT 'user',
    profile_image VARCHAR(255) NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* ===============================
   SESSIONS
================================ */
CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token CHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* ===============================
   WORKOUTS
================================ */
CREATE TABLE workouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    activity ENUM('running','cycling','weightlifting','yoga','swimming') NOT NULL,
    duration_minutes INT NOT NULL,
    distance_km DECIMAL(6,2) NULL,
    calories INT NOT NULL,
    notes TEXT NULL,
    location VARCHAR(64) NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_workouts_user (user_id, recorded_at)
);

/* ===============================
   GOALS
================================ */
CREATE TABLE goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type ENUM('distance','duration','workouts') NOT NULL,
    target_value DECIMAL(8,2) NOT NULL,
    current_value DECIMAL(8,2) NOT NULL DEFAULT 0,
    deadline DATE NOT NULL,
    title VARCHAR(120) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* ===============================
   TRIGGER: UPDATE GOALS
================================ */
DROP TRIGGER IF EXISTS trg_workout_goal_update;
DELIMITER $$

CREATE TRIGGER trg_workout_goal_update
AFTER INSERT ON workouts
FOR EACH ROW
BEGIN
    UPDATE goals
    SET current_value =
        IF(type = 'distance',
            current_value + IFNULL(NEW.distance_km, 0),
        IF(type = 'duration',
            current_value + NEW.duration_minutes,
        IF(type = 'workouts',
            current_value + 1,
            current_value)))
    WHERE user_id = NEW.user_id;
END$$

DELIMITER ;

/* ===============================
   ADMIN DEFAULT USER
   Password: Admin@123
   (replace hash with a real one)
================================ */

INSERT INTO users
(name, email, password_hash, date_of_birth, current_weight_kg, gender, role, profile_image)
VALUES (
  'Admin',
  'admin@fitness.local',
  '$2y$10$REPLACE_WITH_REAL_PASSWORD_HASH',
  '1990-01-01',
  70.00,
  'male',
  'admin',
  ''
)
ON DUPLICATE KEY UPDATE email = email;
