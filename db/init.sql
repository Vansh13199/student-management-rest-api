-- Optional setup script. The app creates the database and table itself
-- (createDatabaseIfNotExist=true + ddl-auto=update), so this is only needed
-- if you want to set things up manually, e.g. for a restricted MySQL user.
--
-- Run:  mysql -u root -p < db/init.sql

CREATE DATABASE IF NOT EXISTS student_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_db;

CREATE TABLE IF NOT EXISTS students (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    name   VARCHAR(100) NOT NULL,
    email  VARCHAR(150) NOT NULL,
    course VARCHAR(100) NOT NULL,
    age    INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
