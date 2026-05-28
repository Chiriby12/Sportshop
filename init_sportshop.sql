-- ============================================================
-- SportShop — Script de inicialización de base de datos
-- PostgreSQL 18
--
-- Ejecutar UNA SOLA VEZ antes de levantar los microservicios.
-- Los microservicios crean las tablas solos (ddl-auto: update).
--
-- Cómo ejecutar:
--   psql -U postgres -f init_sportshop.sql
-- O pégalo directamente en pgAdmin > Query Tool.
-- ============================================================

-- 1. Crear la base de datos (si no existe)
SELECT 'CREATE DATABASE sportshop'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'sportshop'
)\gexec

-- 2. Conectarse a sportshop y crear los 4 esquemas
\c sportshop

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS admin;
CREATE SCHEMA IF NOT EXISTS notifications;

-- 3. Dar permisos al usuario postgres sobre todos los esquemas
GRANT ALL PRIVILEGES ON SCHEMA auth          TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA catalog       TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA admin         TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA notifications TO postgres;

-- Listo. Ahora levanta los microservicios en este orden:
--   1. sportshop-auth        (puerto 8080)
--   2. sportshop-notifications (puerto 8083)
--   3. sportshop-catalog     (puerto 8081)
--   4. sportshop-admin       (puerto 8082)
--
-- Spring creará las tablas automáticamente (ddl-auto: update).
