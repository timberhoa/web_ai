-- ============================================
-- Migration: Add Face Recognition Columns (Fixed)
-- ============================================
-- MySQL 8.0 compatible version
-- Date: 2025-12-04

USE db_1;

-- 1. Thêm các column mới vào bảng users
-- Note: Bỏ IF NOT EXISTS vì MySQL 8.0 không support trong ALTER TABLE
ALTER TABLE users 
ADD COLUMN face_vector JSON NULL COMMENT 'Face embedding vector (512 dimensions)',
ADD COLUMN face_registered BOOLEAN DEFAULT FALSE COMMENT 'Face registration status',
ADD COLUMN face_quality_score FLOAT NULL COMMENT 'Face quality score when registered',
ADD COLUMN face_registered_at DATETIME NULL COMMENT 'Face registration timestamp';

-- 2. Tạo index để tăng tốc query
CREATE INDEX idx_face_registered ON users(face_registered);

-- 3. Verify changes
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'db_1' 
  AND TABLE_NAME = 'users'
  AND COLUMN_NAME LIKE 'face%'
ORDER BY ORDINAL_POSITION;
