-- Create database if not exists
CREATE DATABASE IF NOT EXISTS java_ambassadors_club;

-- Use the database
USE java_ambassadors_club;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20),
    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 