-- MySQL schema for Bank Transaction System
-- Run this script to create the database and tables

CREATE DATABASE IF NOT EXISTS bank_system;
USE bank_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password_encrypted VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    age INT,
    phone_encrypted VARCHAR(255),
    email_encrypted VARCHAR(255),
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    last_transaction_amount DECIMAL(15,2) DEFAULT 0.00,
    daily_transaction_count INT DEFAULT 0,
    failed_login_attempts INT DEFAULT 0,
    lock_timestamp DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'LOAN') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status ENUM('APPROVED', 'REJECTED') NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    timestamp DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Create admin user (optional - you can create this through the application)
-- INSERT INTO users (username, password_encrypted, full_name, gender, age, phone_encrypted, email_encrypted, role)
-- VALUES ('admin', 'encrypted_password_here', 'System Administrator', 'Other', 30, 'encrypted_phone', 'encrypted_email', 'ADMIN');