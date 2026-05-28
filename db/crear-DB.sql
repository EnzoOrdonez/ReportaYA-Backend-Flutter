-- 1) Crear la base de datos
CREATE DATABASE reportaya;

-- 2) Crear el usuario de la aplicación
-- Cambia la contraseña en producción
CREATE USER reportaya_user WITH ENCRYPTED PASSWORD 'secret123';

-- 3) Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE reportaya TO reportaya_user;




