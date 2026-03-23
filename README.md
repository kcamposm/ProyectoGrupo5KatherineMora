Profe, ir al main y darle correr pero antes conectar a su base de datos SQL.

--Profe leer!!

Para la base de datos el script de SQL:

CREATE DATABASE inventario_juegos;
USE inventario_juegos;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precio DECIMAL(10,2) NOT NULL,
    cantidad_jugadores INT NOT NULL,
    duracion_minutos INT NOT NULL,
    edad_minima INT NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    imagen_producto VARCHAR(255)
);

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    prioridad INT NOT NULL CHECK (prioridad BETWEEN 1 AND 3)
);

CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_nombre VARCHAR(100) NOT NULL,
    prioridad INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE venta_detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id)
);
