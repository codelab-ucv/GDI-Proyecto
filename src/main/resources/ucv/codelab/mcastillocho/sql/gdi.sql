BEGIN TRANSACTION;

PRAGMA foreign_keys = ON;

CREATE TABLE
    IF NOT EXISTS "empresa" (
        "id_empresa" INTEGER PRIMARY KEY NOT NULL,
        "nombre_empresa" TEXT NOT NULL,
        "ruc" TEXT NOT NULL,
        "email_empresa" TEXT,
        "ubicacion" TEXT,
        "logo" TEXT,
        -- Restricción única para evitar duplicados en nombre + RUC
        UNIQUE ("nombre_empresa", "ruc")
    );

CREATE TABLE
    IF NOT EXISTS "cliente" (
        "id_cliente" INTEGER PRIMARY KEY NOT NULL,
        "nombre_cliente" TEXT NOT NULL,
        "dni_cliente" TEXT NOT NULL UNIQUE,
        "telefono" TEXT,
        "email_cliente" TEXT
    );

CREATE TABLE
    IF NOT EXISTS "trabajador" (
        "id_trabajador" INTEGER PRIMARY KEY NOT NULL,
        "nombre_trabajador" TEXT NOT NULL,
        "dni_trabajador" TEXT NOT NULL UNIQUE,
        "puesto" TEXT NOT NULL,
        "tipo_letra" TEXT,
        "color_fondo" TEXT,
        "color_boton" TEXT
    );

CREATE TABLE
    IF NOT EXISTS "producto" (
        "id_producto" INTEGER PRIMARY KEY NOT NULL,
        "nombre_producto" TEXT NOT NULL,
        "precio" REAL NOT NULL,
        "vigente" INTEGER NOT NULL DEFAULT 1
    );

CREATE TABLE
    IF NOT EXISTS "orden" (
        "id_orden" INTEGER PRIMARY KEY NOT NULL,
        "id_trabajador" INTEGER NOT NULL,
        "id_cliente" INTEGER NOT NULL,
        "id_empresa" INTEGER NOT NULL,
        "fecha_orden" TEXT NOT NULL,
        FOREIGN KEY ("id_trabajador") REFERENCES "trabajador" ("id_trabajador"),
        FOREIGN KEY ("id_cliente") REFERENCES "cliente" ("id_cliente"),
        FOREIGN KEY ("id_empresa") REFERENCES "empresa" ("id_empresa")
    );

CREATE TABLE
    IF NOT EXISTS "sub_orden" (
        "id_sub_orden" INTEGER PRIMARY KEY NOT NULL,
        "id_orden" INTEGER NOT NULL,
        "id_producto" INTEGER NOT NULL,
        "cantidad" INTEGER NOT NULL,
        FOREIGN KEY ("id_orden") REFERENCES "orden" ("id_orden"),
        FOREIGN KEY ("id_producto") REFERENCES "producto" ("id_producto")
        -- Evita que una orden añada un mismo producto dos veces
        UNIQUE ("id_orden", "id_producto")
    );

COMMIT;