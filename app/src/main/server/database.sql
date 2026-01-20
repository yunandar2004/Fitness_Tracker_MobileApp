-- Create database
CREATE DATABASE IF NOT EXISTS fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitness_db;

-- Users
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  date_of_birth DATE NOT NULL,
  current_weight_kg DECIMAL(5,2) NOT NULL,
  gender ENUM('male','female') NOT NULL,
  role ENUM('admin','user') NOT NULL DEFAULT 'user',
  profile_image VARCHAR(255) DEFAULT '',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL,
  deleted_at DATETIME NULL DEFAULT NULL
);

-- Sessions (track login times)
CREATE TABLE IF NOT EXISTS sessions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  logout_time DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Goals
CREATE TABLE IF NOT EXISTS goals (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  title VARCHAR(150) NOT NULL,
  period ENUM('day','month','duration') NOT NULL,
  duration_minutes INT NULL,
  start_date DATE NULL,
  end_date DATE NULL,
  status ENUM('current','completed','reset') NOT NULL DEFAULT 'current',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Workouts
CREATE TABLE IF NOT EXISTS workouts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  activity ENUM('Running','Walking','Cycling','Weightlifting','Swimming','Yoga','HIIT') NOT NULL,
  heart_rate INT NOT NULL,
  time_minutes INT NOT NULL,
  burned_calories DECIMAL(10,2) NOT NULL,
  notes VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Admin default user (change email/password after first login)
INSERT INTO users (name, email, password_hash, date_of_birth, current_weight_kg, gender, role, profile_image)
VALUES (
  'Admin',
  'admin@fitness.local',
  -- password: Admin@123
  '$2y$10$Qe8bqvQmQmQmQmQmQmQmQeQmQmQmQmQmQmQmQmQmQmQmQmQmQmQm',
  '1990-01-01',
  70.00,
  'male',
  'admin',
  ''
)
ON DUPLICATE KEY UPDATE email=email;
